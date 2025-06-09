package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/02
 */
@Data
public class WorkOrderDeleteParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;
}
