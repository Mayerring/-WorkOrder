package com.example.spring_vue_demo.vo;

import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Department;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationStructureVO {
    @Schema(description = "公司信息")
    private Company company;
    @Schema(description = "部门信息")
    List<Department> departments;
}
