package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author wtt
 * @date 2025/06/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusDataParam {
    @NotNull
    @Schema(description = "时间 (1:周，2：月，3：年)")
    private Integer timeType;
}
