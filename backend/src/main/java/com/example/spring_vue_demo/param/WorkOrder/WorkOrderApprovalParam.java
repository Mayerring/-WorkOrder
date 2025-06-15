package com.example.spring_vue_demo.param.WorkOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author WangDayu
 * @date 2025/6/9
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderApprovalParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "操作备注")
    private String remark;

    @Schema(description = "审核意见")
    private Boolean isApproved;
}
