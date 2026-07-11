package com.example.spring_vue_demo.param;

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
public class AddStaffParam {
    @Schema(description = "员工姓名")
    private String name;
    @Schema(description = "员工所属公司名")
    private String companyName;
    @Schema(description = "所属部门名字")
    private String departmentName;
    @Schema(description = "员工职位")
    private String position;
    @Schema(description = "主管名字")
    private String managerName;
    @Schema(description = "电话号码")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
}
