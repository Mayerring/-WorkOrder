package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wtt
 * @date 2025/05/24
 */
public class WorkOrderDetailParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;
}
