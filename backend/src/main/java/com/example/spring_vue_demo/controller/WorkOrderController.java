package com.example.spring_vue_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderHandleParam;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author wtt
 * @date 2025/05/24
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "工单管理")
@RequestMapping("/workOrder")
public class WorkOrderController {
    @Autowired
    private WorkOrderService workOrderService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "分页")
    @PostMapping("/page")
    public IPage<WorkOrderPageVO> page(@RequestBody WorkOrderPageParam param) {
        return workOrderService.pageWorkOrder(param);
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "详情")
    @PostMapping("/detail")
    public WorkOrderDetailVO detail(@RequestBody WorkOrderDetailParam param) {
        return workOrderService.detail(param);
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "工单操作")
    @PostMapping("/handle")
    public WorkOrderUpdateStatusVO updateWorkOrderStatus(@RequestBody WorkOrderHandleParam param) {
        return workOrderService.handleWorkOrder(param);
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public WorkOrderUpdateStatusVO delete(@RequestBody WorkOrderDeleteParam param) {
        return workOrderService.deleteOrder(param);
    }

    @ApiOperationSupport(order = 5)
    @Operation(summary = "取消")
    @PostMapping("/cancel")
    public WorkOrderUpdateStatusVO cancel(@RequestBody WorkOrderCancelParam param) {
        return workOrderService.cancel(param);
    }


    @ApiOperationSupport(order = 6)
    @Operation(summary = "新建工单")
    @PostMapping("/create")
    public Result create(@RequestBody WorkOrderCreateParam param) {
        return workOrderService.create(param);
    }

    @ApiOperationSupport(order = 7)
    @Operation(summary = "指定审核人")
    @PostMapping("/dispatch")
    public Result dispatchToAuditor()
    {
        return null;
    }
}
