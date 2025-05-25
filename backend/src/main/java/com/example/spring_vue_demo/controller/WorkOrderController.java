package com.example.spring_vue_demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderHelpParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderUpdateStatusParam;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author wtt
 * @date 2025/05/24
 */
@RestController
@AllArgsConstructor
@Tag(name="工单管理")
@RequestMapping("/workOrder")
public class WorkOrderController {
    @Autowired
    private static WorkOrderService workOrderService;

    @Operation(summary = "分页")
    @PostMapping("/page")
    public IPage<WorkOrderPageVO> page(@RequestBody IPage<WorkOrderPageParam> param){
        return workOrderService.pageWorkOrder(param);
    }

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

    @Operation(summary = "处理完成")
    @PostMapping("/finish")
    public WorkOrderUpdateStatusVO finishWorkOrder(@RequestBody WorkOrderUpdateStatusParam param){
        return workOrderService.finishWorkOrder(param);
    }

    @Operation(summary = "验收")
    @PostMapping("/check")
    public WorkOrderUpdateStatusVO checkWorkOrder(@RequestBody WorkOrderUpdateStatusParam param){
        return workOrderService.checkWorkOrder(param);
    }

    @Operation(summary="申请协助处理")
    @PostMapping("/applyHelp")
    public Object applyHelp(@RequestBody WorkOrderHelpParam param){
        return workOrderService.applyHelp(param);
    }

    @Operation(summary = "催单")
    @PostMapping("/urgeOrder")
    public Object urgeOrder(@RequestBody Object workOrderUrgeOrderParam){
        return workOrderService.urgeOrder(workOrderUrgeOrderParam);
    }


    @Operation(summary = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody Object param){
        return workOrderService.deleteOrder(param);
    }

    @Operation(summary = "取消")
    @PostMapping("/cancel")
    public Object cancel(@RequestBody Object param){
        return workOrderService.cancel(param);
    }


}
