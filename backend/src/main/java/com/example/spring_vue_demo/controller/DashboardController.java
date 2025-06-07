package com.example.spring_vue_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spring_vue_demo.param.MessageParam;
import com.example.spring_vue_demo.param.StatusDataParam;
import com.example.spring_vue_demo.param.WeekHandleQuantityParam;
import com.example.spring_vue_demo.service.DashboardService;
import com.example.spring_vue_demo.vo.*;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/25
 */
@RestController
@RequiredArgsConstructor
@Tag(name="工作台")
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "数据看板")
    @PostMapping("/data")
    public WorkOrderDataVO getData(){
        return dashboardService.getData();
    }
    @ApiOperationSupport(order = 2)
    @Operation(summary = "待办事项")
    @PostMapping("/todo")
    public List<WorkOrderTodoVO> getTodo(){
        return dashboardService.getTodo();
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "工单状态统计")
    @PostMapping("/status")
    public List<StatusDataVO>  countStatus(@RequestBody StatusDataParam param){
        return dashboardService.getStatus(param);
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "本周处理数量")
    @PostMapping("/handleQuantity")
    public WeekHandleVO getHandleQuantity(@RequestBody WeekHandleQuantityParam param){
        return dashboardService.getWeekHandleQuantity(param);
    }

    @ApiOperationSupport(order = 5)
    @Operation(summary = "消息中心")
    @PostMapping("/pageMessages")
    public IPage<MessageVO> pageMessages(@RequestBody MessageParam param){
        return dashboardService.pageMessages(param);
    }
}
