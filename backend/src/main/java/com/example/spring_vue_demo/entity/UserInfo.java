package com.example.spring_vue_demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Data
@AllArgsConstructor
public class UserInfo {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "公司id")
    private Long companyId;

    @Schema(description = "公司名")
    private String companyName;

    @Schema(description = "部门id")
    private Long departmentId;

    @Schema(description = "部门名")
    private String departmentName;

    @Schema(description = "操作时间")
    private Long handleTime;
}
