package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/31
 */
@Data
public class HandleUserInfoParam {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "公司编码")
    private String companyCode;

    @Schema(description = "部门编码")
    private String departmentCode;

    @Schema(description = "已处理")
    private Boolean finished;
}
