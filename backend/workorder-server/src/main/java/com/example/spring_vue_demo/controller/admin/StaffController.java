package com.example.spring_vue_demo.controller.admin;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddStaffParam;
import com.example.spring_vue_demo.param.ChangeStaffInfoParam;
import com.example.spring_vue_demo.param.StaffPageParam;
import com.example.spring_vue_demo.service.AdminService;
import com.example.spring_vue_demo.service.StaffService;
import com.example.spring_vue_demo.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name="职工管理")
@RequestMapping("/admin/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @ApiOperationSupport(order = 1)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "新增员工")
    @PostMapping("/add")
    public Result addStaff(@RequestBody AddStaffParam param)
    {
        return staffService.addStaff(param);
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "删除员工")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result deleteStaff(@RequestParam Long id)
    {
        return staffService.delete(id);
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "修改员工信息")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/change")
    public Result changeStaff(@RequestBody ChangeStaffInfoParam param)
    {
        return staffService.changeStaff(param);
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "分页查看员工信息")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/page")
    public Result staffChange(@RequestBody StaffPageParam param)
    {
        return staffService.staffPage(param);
    }

    @ApiOperationSupport(order = 5)
    @Operation(summary = "所有员工")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Result all(){
        return staffService.allStaff();
    }
}
