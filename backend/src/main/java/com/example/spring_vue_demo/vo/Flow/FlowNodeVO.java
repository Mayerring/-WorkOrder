package com.example.spring_vue_demo.vo.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/15
 */
@Data
public class FlowNodeVO {
    @Schema(description = "节点id")
    private Long id;

    @Schema(description = "节点类型:审批2，指派3，验收5")
    private Integer nodeType;

    @Schema(description = "节点类型desc")
    private String nodeTypeDesc;

    @Schema(description = "处理人id")
    private Long handlerId;

    @Schema(description = "处理人名")
    private String handlerName;

    @Schema(description = "是否为该流程终止节点")
    private Boolean isLastNode;
}
