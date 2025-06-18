package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/18
 */
@Data
public class TypeDataVO {
    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "类型desc")
    private String typeDesc;

    @Schema(description = "数量")
    private Long quantity;
}
