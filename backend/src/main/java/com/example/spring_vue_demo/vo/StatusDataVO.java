package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/05
 */
@Data
public class StatusDataVO {
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态desc")
    private String statusDesc;

    @Schema(description = "数量")
    private Long quantity;
}
