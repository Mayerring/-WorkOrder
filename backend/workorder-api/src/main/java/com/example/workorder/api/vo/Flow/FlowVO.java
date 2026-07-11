package com.example.workorder.api.vo.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FlowVO {
    @Schema(description = "流程ID")
    private String flowId;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "节点列表")
    private List<FlowNodeVO> nodes;
}