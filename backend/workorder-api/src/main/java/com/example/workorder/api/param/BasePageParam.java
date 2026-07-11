package com.example.workorder.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BasePageParam {
    @NotNull
    @Schema(description = "当前页数")
    private Integer pageNum;
    
    @NotNull
    @Schema(description = "每页大小")
    private Integer pageSize;
}