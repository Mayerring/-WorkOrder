package com.example.spring_vue_demo.vo.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @date 2025/06/14
 */
@Data
public class FlowVO {
    @Schema(description = "流程id")
    private Long flowId;

    @Schema(description = "节点")
    private List<FlowNodeVO> nodes;
}
