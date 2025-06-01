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

    @Schema(description = "操作类型")
    private Integer handleType;

    @Schema(description = "公司id")
    private Long companyId;

    @Schema(description = "部门id")
    private Long departmentId;
}
