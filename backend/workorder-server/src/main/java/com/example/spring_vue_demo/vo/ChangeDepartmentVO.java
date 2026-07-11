package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDepartmentVO {
    @Schema(description = "部门id")
    private Long id;
    @Schema(description = "部门编码")
    private String code;
    @Schema(description = "上级部门编码")
    private String parentDepartmentCode;
    @Schema(description = "主管编码")
    private String leaderNumber;
    @Schema(description = "修改结果")
    private Boolean isSuccess;
}
