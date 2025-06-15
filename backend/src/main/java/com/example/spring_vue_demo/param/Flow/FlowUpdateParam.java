package com.example.spring_vue_demo.param.Flow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wtt
 * @date 2025/06/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FlowUpdateParam extends FlowCreateParam{
    @Schema(description = "流程Id")
    private Long flowId;
}
