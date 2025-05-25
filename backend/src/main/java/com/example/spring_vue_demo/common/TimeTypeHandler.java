package com.example.spring_vue_demo.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author wtt
 * @date 2025/05/25
 */
public class TimeTypeHandler extends BaseTypeHandler<String> {

   private final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将字符串日期解析为LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(parameter, formatter);
            // 转换为Instant然后获取秒数
            long epochSecond = dateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            // 将秒数设置为BigInt参数
            ps.setLong(i, epochSecond);
        } catch (DateTimeParseException e) {
            throw new SQLException("Error parsing date: " + parameter, e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long time = rs.getLong(columnName);
        return formatter.format(Instant.ofEpochSecond(time));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long time = rs.getLong(columnIndex);
        return formatter.format(Instant.ofEpochSecond(time));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long time = cs.getLong(columnIndex);
        return formatter.format(Instant.ofEpochSecond(time));
    }
}
