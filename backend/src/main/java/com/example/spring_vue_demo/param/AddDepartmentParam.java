package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDepartmentParam {
    @Schema(description = "部门名")
    private String name;
    @Schema(description = "上级部门名")
    private String parentDepartmentName;
    @Schema(description = "所属公司名")
    private String companyName;
    @Schema(description = "部门领导名")
    private String leaderName;
}
