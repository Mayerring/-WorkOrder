package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDepartmentVO {
    @Schema(description = "部门id")
    private int id;
    @Schema(description = "部门编码")
    private String departmentCode;
    @Schema(description = "若有部门主管的主管编码")
    private String leaderNumber;
}
