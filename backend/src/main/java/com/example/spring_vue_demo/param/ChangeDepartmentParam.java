package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDepartmentParam {
    @Schema(description = "部门id")
    private Long id;
    @Schema(description = "部门编码")
    private String code;
    @Schema(description = "上级部门编码")
    private String parentDepartmentCode;
    @Schema(description = "主管编码")
    private String leaderNumber;

}
