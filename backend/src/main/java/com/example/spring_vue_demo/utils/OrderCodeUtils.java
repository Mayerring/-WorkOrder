package com.example.spring_vue_demo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderCodeUtils {
    public static String generateWorkOrderCode() {
        String prefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long snowflakeId = cn.hutool.core.util.IdUtil.getSnowflake(1, 1).nextId(); // 机器ID=1, 数据中心ID=1
        return prefix+snowflakeId;
    }
}
