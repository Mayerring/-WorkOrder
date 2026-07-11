package com.example.spring_vue_demo.common;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wtt
 * @date 2025/06/16
 */
public class ThreadPoolManager {
    // 使用枚举实现单例(线程安全)
    private enum Singleton {
        INSTANCE;

        private final ThreadPoolManager instance = new ThreadPoolManager();

        public ThreadPoolManager getInstance() {
            return instance;
        }
    }

    // 线程池容器
    private final Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    private ThreadPoolManager() {
        // 初始化默认线程池
        initDefaultPools();
    }

    public static ThreadPoolManager getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    // 项目启动时初始化默认线程池
    private void initDefaultPools() {
        // 导出专用线程池
        registerThreadPool("exportPool", createThreadPool(4, 4, 60));
    }

    // 创建线程池的模板方法
    private ExecutorService createThreadPool(int coreSize, int maxSize, long keepAliveTime) {
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 注册线程池
    private void registerThreadPool(String poolName, ExecutorService executorService) {
        threadPoolMap.put(poolName, executorService);
    }

    // 获取线程池
    public ExecutorService getThreadPool(String poolName) {
        return threadPoolMap.get(poolName);
    }
    //关闭线程池
    public void shutdown(String poolName){
        threadPoolMap.get(poolName).shutdown();
    }

    // 关闭所有线程池
    public void shutdownAll() {
        threadPoolMap.values().forEach(executor -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
    }
}
