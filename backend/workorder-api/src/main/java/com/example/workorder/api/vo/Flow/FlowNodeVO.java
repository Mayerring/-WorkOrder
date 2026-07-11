package com.example.workorder.api.vo.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlowNodeVO {
    @Schema(description = "节点ID")
    private String nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "节点类型")
    private Integer nodeType;
}