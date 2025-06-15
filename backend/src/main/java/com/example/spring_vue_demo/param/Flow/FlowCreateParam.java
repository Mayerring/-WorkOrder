package com.example.spring_vue_demo.param.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @date 2025/06/14
 */
@Data
public class FlowCreateParam {

    @NotNull
    @Schema(description = "流程名")
    private String flowName;

    @NotNull
    @Schema(description = "审核节点")
    private List<FlowNode>nodes;

    @NotNull
    @Schema(description = "分配节点")
    private FlowNode distributeNode;

    @NotNull
    @Schema(description = "验收节点")
    private FlowNode checkNode;
}
