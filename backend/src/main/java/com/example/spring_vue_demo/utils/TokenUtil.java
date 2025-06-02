package com.example.spring_vue_demo.utils;

import com.example.spring_vue_demo.entity.Staff;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author WangDayu
 * @date 2025/6/2
 */
public class TokenUtil {

    // 建议至少 256-bit（32 字节）长度密钥
    private static final String SECRET = "MySuperSecretKeyForJWT1234567890!"; // >= 32 字符
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 JWT Token
     */
    public static String generateToken(Staff staff) {
        return Jwts.builder()
                .setSubject(staff.getName())
                .claim("id", staff.getId())
                .claim("name", staff.getName())
                .claim("company", staff.getCompany())
                .claim("department", staff.getDepartment())
                .claim("position", staff.getPosition())
                .claim("status", staff.getStatus())
                .claim("phone", staff.getPhone())
                .claim("email", staff.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天有效期
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证 Token 是否有效
     */
    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中解析出 Staff 信息
     */
    public static Staff parsestaffFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Staff staff = new Staff();
        staff.setId(Long.valueOf(claims.get("id").toString()));
        staff.setName(claims.getSubject());
        staff.setCompany(claims.get("company").toString());
        staff.setDepartment(claims.get("department").toString());
        staff.setPosition(claims.get("position").toString());
        staff.setStatus((Integer) claims.get("status"));
        staff.setEmail(claims.get("email").toString());
        staff.setPhone(claims.get("phone").toString());

        return staff;
    }
}
