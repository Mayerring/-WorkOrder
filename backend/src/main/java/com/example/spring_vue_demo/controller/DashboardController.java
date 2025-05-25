package com.example.spring_vue_demo.controller;

import com.example.spring_vue_demo.service.DashboardService;
import com.example.spring_vue_demo.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wtt
 * @date 2025/05/25
 */
@RestController
@AllArgsConstructor
@Tag(name="工作台")
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private static DashboardService dashboardService;

    @Operation(summary = "数据看板")
    @PostMapping("/data")
    public Object getData(){
        return dashboardService.getData();
    }
    @Operation(summary = "待办事项")
    @PostMapping("/todo")
    public Object getTodo(@RequestBody Object param){
        return dashboardService.getTodo(param);
    }

    @Operation(summary = "工单状态统计")
    @PostMapping("/status")
    public Object countStatus(@RequestBody Object param){
        return dashboardService.getStatus(param);
    }

    @Operation(summary = "本周处理数量")
    @PostMapping("/handleQuantity")
    public Object getHandleQuantity(@RequestBody Object param){
        return dashboardService.getHandleQuantity(param);
    }

    @Operation(summary = "消息中心")
    @PostMapping("/getMessages")
    public Object getMessages(@RequestBody Object param){
        return dashboardService.getMessages(param);
    }
}
