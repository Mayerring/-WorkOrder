package com.example.spring_vue_demo.config;

import com.example.spring_vue_demo.common.ThreadPoolManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wtt
 * @date 2025/06/16
 */
@Configuration
public class ThreadPoolInitializer {

    @Bean
    public CommandLineRunner initThreadPools() {
        return args -> {
            // 初始化时会自动创建默认线程池
            ThreadPoolManager.getInstance();
            System.out.println("线程池初始化完成");
        };
    }
}
