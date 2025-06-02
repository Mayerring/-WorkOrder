package com.example.spring_vue_demo.param;

import com.example.spring_vue_demo.enums.HandleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Data
public class WorkOrderUpdateStatusParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @NotNull
    @Schema(description = "操作,1:分配，2：请求协助，3：催单，4：完成，5：确认完成，6：仍有问题")
    private Integer handleType;

    @Schema(description = "安排的用户Id（分配处理人/协助处理人）")
    private Long assignedUserId;

}
