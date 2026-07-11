package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStaffVO {
    @Schema(description = "员工id")
    private long id;
    @Schema(description = "员工编码")
    private String staffNumber;
    @Schema(description = "所属公司编码")
    private String companyCode;
    @Schema(description = "所属部门编码")
    private String departmentCode;
    @Schema(description = "主管编码")
    private String managerNUmber;
}
