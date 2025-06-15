package com.example.spring_vue_demo.vo.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/15
 */
@Data
@AllArgsConstructor
public class FlowCreateVO {
    @Schema(description = "流程id")
    private Long flowId;

    @Schema(description = "成功")
    private boolean success;
}
