package com.example.spring_vue_demo.controller.admin;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddStaffParam;
import com.example.spring_vue_demo.service.AdminService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name="职工管理")
@RequestMapping("/admin/staff")
public class StaffController {

    @Autowired
    private AdminService adminService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "新增员工")
    @PostMapping("/add")
    public Result addStaff(@RequestBody AddStaffParam param)
    {
        return adminService.addStaff(param);
        //return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "删除员工")
    @PostMapping("/delete")
    public Result deleteStaff()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "修改员工信息")
    @PostMapping("/change")
    public Result changeStaff()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "分页查看员工信息")
    @PostMapping("/page")
    public Result staffChange()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 5)
    @Operation(summary = "所有员工")
    @PostMapping("/all")
    public Result all(){
        return Result.error("暂未实现");
    }
}
