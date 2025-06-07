package com.example.spring_vue_demo.controller.User;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "根据查询员工的信息")
    @PostMapping("belong")
    public Result belong(Long staffId) {
        return userService.belong(staffId);
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "修改个人信息")
    @PostMapping("/change")
    public Result changeInfo()
    {
        return Result.error("暂未实现");
    }
}
