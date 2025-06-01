package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/31
 */
@Data
public class BasePageParam {
    // 分页参数
    @NotNull
    @Schema(description = "当前页数")
    private Integer pageNum;
    @NotNull
    @Schema(description = "每页大小")
    private Integer pageSize;
}
