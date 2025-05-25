package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderHelpParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderUpdateStatusParam;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import org.springframework.stereotype.Service;

@Service
public interface WorkOrderService extends IService<WorkOrder> {

    IPage<WorkOrderPageVO> pageWorkOrder(IPage<WorkOrderPageParam> param);

    WorkOrderDetailVO detail(WorkOrderDetailParam param);

    WorkOrderUpdateStatusVO finishWorkOrder(WorkOrderUpdateStatusParam param);

    WorkOrderUpdateStatusVO passWorkOrder(WorkOrderUpdateStatusParam param);

    Object applyHelp(WorkOrderHelpParam param);


    Object urgeOrder(Object workOrderUrgeOrderParam);

    Object deleteOrder(Object param);

    Object cancel(Object param);
}
