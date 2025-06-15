package com.example.spring_vue_demo.param.WorkOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
public class WorkOrderDetailParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;
}
