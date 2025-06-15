package com.example.spring_vue_demo.vo.WorkOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author wtt
 * @date 2025/05/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderUpdateStatusVO {
    @Schema(description = "成功flag")
    private boolean success;
    @Schema(description = "处理工单id")
    private Long id;
    @Schema(description = "处理工单code")
    private String code;
}
