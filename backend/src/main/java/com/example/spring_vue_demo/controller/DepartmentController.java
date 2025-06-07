package com.example.spring_vue_demo.controller;

import com.example.spring_vue_demo.entity.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name="部门管理")
@RequestMapping("department")
public class DepartmentController {

    @ApiOperationSupport(order = 1)
    @Operation(summary = "新增员工")
    @PostMapping("/add")
    public Result showStructure(){
        return Result.error("暂未实现");
    }

    public Result selectByDepartmentId(Integer departmentId){
        return Result.error("暂时未实现");
    }
}
