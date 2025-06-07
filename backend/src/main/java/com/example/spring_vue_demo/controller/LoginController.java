package com.example.spring_vue_demo.controller;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.param.LoginParam;
import com.example.spring_vue_demo.service.LoginService;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author WangDayu
 * @date 2025/6/1
 */

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginParam loginParam) {
        return loginService.login(loginParam);
    }
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取个人信息")
    @PostMapping("/me")
    public Result me() {
        Staff staff = StaffHolder.get();
        return Result.success(staff);
    }
}
