package com.example.spring_vue_demo.param.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @date 2025/06/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowIdParam {
    @NotNull
    @Schema(description = "流程id")
    private Long flowId;
}
