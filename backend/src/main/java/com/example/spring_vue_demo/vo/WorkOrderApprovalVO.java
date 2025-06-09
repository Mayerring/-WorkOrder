package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderApprovalVO {
    @Schema(description = "审核结果，0表示结束，1表示未结束")
    private Boolean result;

    @Schema(description = "审核意见,是否通过")
    private Boolean isPassed;

    @Schema(description = "下一个审核人")
    private String nextAuditNumber;
}
