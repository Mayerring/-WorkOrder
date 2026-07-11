package com.example.spring_vue_demo.vo.WorkOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/03
 */
@Data
public class WorkOrderDataVO {
    @Schema(description = "本月已处理工单数")
    private Long monthFinishedNum;

    @Schema(description = "待处理工单数")
    private Long unHandledNum;

    @Schema(description = "未审核工单数")
    private Long unAuditedNum;

    @Schema(description = "超时工单数")
    private Long delayNum;

}
