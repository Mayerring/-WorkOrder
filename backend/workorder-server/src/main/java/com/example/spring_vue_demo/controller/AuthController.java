package com.example.spring_vue_demo.controller;

import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.utils.TokenUtil;
import com.example.workorder.api.dto.ValidateTokenResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/validate")
    public ValidateTokenResult validateToken(@RequestHeader("Authorization") String token) {
        log.info("AuthController.validateToken called, token length: {}, token start: {}", 
                token != null ? token.length() : 0, 
                token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token);
        
        if (token == null || token.trim().isEmpty()) {
            log.warn("Token is null or empty");
            return new ValidateTokenResult(false, null, null, "Token不能为空");
        }
        
        boolean verified = TokenUtil.verifyToken(token);
        log.info("TokenUtil.verifyToken result: {}", verified);
        
        if (verified) {
            try {
                Staff staff = TokenUtil.parsestaffFromToken(token);
                log.info("Token parsed successfully, userId: {}, role: {}", staff.getId(), staff.getRole());
                return new ValidateTokenResult(true, String.valueOf(staff.getId()), staff.getRole(), "Token验证成功");
            } catch (Exception e) {
                log.error("Token parse error: {}", e.getMessage());
                return new ValidateTokenResult(false, null, null, "Token解析失败: " + e.getMessage());
            }
        } else {
            log.warn("Token verification failed");
            return new ValidateTokenResult(false, null, null, "Token无效或已过期");
        }
    }
}