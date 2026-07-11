package com.example.workorder.api.param.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowIdParam {
    @NotNull
    @Schema(description = "流程id")
    private Long flowId;
}