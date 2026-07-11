package com.example.workorder.api.param.WorkOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WorkOrderDetailParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;
}