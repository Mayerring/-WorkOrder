package com.example.spring_vue_demo.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wtt
 * @date 2025/06/02
 */
@Component
public class TimeFillHandler implements MetaObjectHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void insertFill(MetaObject metaObject) {
        // 设置发送时间为当前时间
        String currentTime = LocalDateTime.now().format(formatter);
        this.strictInsertFill(metaObject, "createTime", String.class, currentTime);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 设置发送时间为当前时间
        String currentTime = LocalDateTime.now().format(formatter);
        this.strictInsertFill(metaObject, "updateTime", String.class, currentTime);
    }
}
