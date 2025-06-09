package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @date 2025/06/05
 */
@Data
@NoArgsConstructor
public class WeekHandleQuantityParam {
    @NotBlank
    @Schema(description = "日期，采用yyyy-MM-dd格式")
    private String date;
}
