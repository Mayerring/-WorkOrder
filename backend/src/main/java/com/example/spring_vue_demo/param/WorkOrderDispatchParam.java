package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDispatchParam {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单类型")
    private int type;

}
