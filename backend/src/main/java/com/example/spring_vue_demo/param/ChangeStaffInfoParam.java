package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStaffInfoParam {
    @NotNull
    @Schema(description = "员工id")
    private Long id;
    @Schema(description = "员工编码")
    private String staffNumber;
    @Schema(description = "所属公司，人员调动时修改")
    private String companyName;
    @Schema(description = "所属部门")
    private String departmentName;
    @Schema(description = "职位")
    private String position;
    @Schema(description = "状态 0 正常 1 休假 2 停职 3 离职")
    private int status;

}
