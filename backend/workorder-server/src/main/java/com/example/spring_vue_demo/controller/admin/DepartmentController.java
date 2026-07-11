package com.example.spring_vue_demo.controller.admin;

import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddDepartmentParam;
import com.example.spring_vue_demo.param.ChangeDepartmentParam;
import com.example.spring_vue_demo.service.DepartmentService;
import com.example.spring_vue_demo.vo.AddDepartmentVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@RestController
@Tag(name="部门管理")
@RequestMapping("admin/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "新增部门")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result addDepartment(@RequestBody AddDepartmentParam param) {
        return departmentService.addDepartment(param);
    }
    @ApiOperationSupport(order = 2)
    @Operation(summary = "查看某个公司的所有部门")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/all")
    public Result allDepartment(@RequestParam String companyName) {
        return departmentService.allDepartmentInCompany(companyName);
    }
    @ApiOperationSupport(order = 3)
    @Operation(summary = "修改部门信息（主管等信息）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/change")
    public Result changeDepartment(@RequestBody ChangeDepartmentParam param) {
        return departmentService.changeDepartment(param);
    }
    @ApiOperationSupport(order = 4)
    @Operation(summary = "删除部门")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result delete(@RequestParam Long id) {
        return departmentService.delete(id);
    }
}
