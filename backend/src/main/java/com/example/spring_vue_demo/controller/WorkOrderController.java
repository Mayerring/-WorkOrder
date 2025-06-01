package com.example.spring_vue_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderHelpParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderUpdateStatusParam;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * @author wtt
 * @date 2025/05/24
 */
@RestController
@RequiredArgsConstructor
@Tag(name="工单管理")
@RequestMapping("/workOrder")
public class WorkOrderController {
    private final WorkOrderService workOrderService;
    @ApiOperationSupport(order = 1)
    @Operation(summary = "分页")
    @PostMapping("/page")
    public IPage<WorkOrderPageVO> page(@RequestBody WorkOrderPageParam param){
        return workOrderService.pageWorkOrder(param);
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "详情")
    @PostMapping("/detail")
    public WorkOrderDetailVO detail(@RequestBody WorkOrderDetailParam param){
        return workOrderService.detail(param);
    }

//    @Operation(summary = "指派")
//    @PostMapping("/distribute")
//    public Object distribute(@RequestBody Object param){
//        return workOrderService.distribute(param);
//    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "处理完成")
    @PostMapping("/finish")
    public WorkOrderUpdateStatusVO finishWorkOrder(@RequestBody WorkOrderUpdateStatusParam param){
        return workOrderService.finishWorkOrder(param);
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "验收")
    @PostMapping("/check")
    public WorkOrderUpdateStatusVO checkWorkOrder(@RequestBody WorkOrderUpdateStatusParam param){
        return workOrderService.checkWorkOrder(param);
    }

    @ApiOperationSupport(order = 5)
    @Operation(summary="申请协助处理")
    @PostMapping("/applyHelp")
    public Object applyHelp(@RequestBody WorkOrderHelpParam param){
        return workOrderService.applyHelp(param);
    }

    @ApiOperationSupport(order = 6)
    @Operation(summary = "催单")
    @PostMapping("/urgeOrder")
    public Object urgeOrder(@RequestBody Object workOrderUrgeOrderParam){
        return workOrderService.urgeOrder(workOrderUrgeOrderParam);
    }

    @ApiOperationSupport(order = 7)
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody Object param){
        return workOrderService.deleteOrder(param);
    }

    @ApiOperationSupport(order = 8)
    @Operation(summary = "取消")
    @PostMapping("/cancel")
    public Object cancel(@RequestBody Object param){
        return workOrderService.cancel(param);
    }


}
