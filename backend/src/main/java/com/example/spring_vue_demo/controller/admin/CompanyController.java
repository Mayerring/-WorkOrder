package com.example.spring_vue_demo.controller.admin;

import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddCompanyParam;
import com.example.spring_vue_demo.service.CompanyService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@RestController
@Tag(name="公司管理")
@RequestMapping("admin/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "新增公司")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result addCompany(@RequestBody AddCompanyParam param) {
        return companyService.addCompany(param);
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "所有公司")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("all")
    public Result allCompany() {
        return companyService.allCompany();
    }
}
