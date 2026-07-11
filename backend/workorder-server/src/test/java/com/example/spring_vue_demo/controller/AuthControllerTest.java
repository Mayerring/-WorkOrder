package com.example.spring_vue_demo.controller;

import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.utils.TokenUtil;
import com.example.workorder.api.dto.ValidateTokenResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    private AuthController authController;
    private String validToken;

    @BeforeEach
    void setUp() {
        authController = new AuthController();
        
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setName("TestUser");
        staff.setRole("admin");
        staff.setCompany("TestCompany");
        staff.setDepartment("TestDept");
        staff.setPosition("Developer");
        staff.setStatus(1);
        staff.setPhone("13800138000");
        staff.setEmail("test@example.com");
        validToken = TokenUtil.generateToken(staff);
    }

    @Test
    void validateToken_ShouldReturnValid_WhenTokenIsValid() {
        ValidateTokenResult result = authController.validateToken(validToken);
        
        assertTrue(result.isValid());
        assertEquals("1", result.getUserId());
        assertEquals("admin", result.getRole());
        assertEquals("Token验证成功", result.getMessage());
    }

    @Test
    void validateToken_ShouldReturnInvalid_WhenTokenIsNull() {
        ValidateTokenResult result = authController.validateToken(null);
        
        assertFalse(result.isValid());
        assertNull(result.getUserId());
        assertNull(result.getRole());
        assertEquals("Token不能为空", result.getMessage());
    }

    @Test
    void validateToken_ShouldReturnInvalid_WhenTokenIsEmpty() {
        ValidateTokenResult result = authController.validateToken("");
        
        assertFalse(result.isValid());
        assertNull(result.getUserId());
        assertNull(result.getRole());
        assertEquals("Token不能为空", result.getMessage());
    }

    @Test
    void validateToken_ShouldReturnInvalid_WhenTokenIsInvalid() {
        ValidateTokenResult result = authController.validateToken("invalid-token-12345");
        
        assertFalse(result.isValid());
        assertNull(result.getUserId());
        assertNull(result.getRole());
        assertEquals("Token无效或已过期", result.getMessage());
    }
}