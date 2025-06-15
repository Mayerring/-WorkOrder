package com.example.spring_vue_demo.param.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/14
 */
@Data
public class FlowNode {

    @NotNull
    @Schema(description = "处理人id")
    private Long handlerId;

    @NotBlank
    @Schema(description = "处理人名")
    private String handlerName;
}
