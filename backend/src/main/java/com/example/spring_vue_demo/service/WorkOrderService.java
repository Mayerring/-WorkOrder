package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;


public interface WorkOrderService extends IService<WorkOrder> {

    IPage<WorkOrderPageVO> pageWorkOrder(WorkOrderPageParam param);

    WorkOrderDetailVO detail(WorkOrderDetailParam param);

    WorkOrderUpdateStatusVO handleWorkOrder(WorkOrderHandleParam param);

    WorkOrderUpdateStatusVO deleteOrder(WorkOrderDeleteParam  param);

    WorkOrderUpdateStatusVO cancel(WorkOrderCancelParam param);

    Result create(WorkOrderCreateParam param);


}
