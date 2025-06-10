package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class WorkOrderDispatchParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @NotNull
    @Schema(description = "操作,1:分配，2：请求协助，3：催单，4：完成，5：确认完成，6：仍有问题 7.创建 8.审核")
    private Integer handleType;

    @Schema(description = "审核人")
    private Long auditId;

}
