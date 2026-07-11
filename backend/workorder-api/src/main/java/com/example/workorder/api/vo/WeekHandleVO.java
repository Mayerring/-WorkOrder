package com.example.workorder.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekHandleVO {
    @Schema(description = "当天工单总数")
    private Long dailyTotalNum;

    @Schema(description = "当天已完成工单总数")
    private Long dailyFinishedNum;

    @Schema(description = "日期")
    private String date;
}