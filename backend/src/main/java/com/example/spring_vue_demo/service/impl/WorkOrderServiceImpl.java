package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderHelpParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderUpdateStatusParam;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import org.springframework.stereotype.Service;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Override
    public IPage<WorkOrderPageVO> pageWorkOrder(IPage<WorkOrderPageParam> param) {
        return null;
    }

    @Override
    public WorkOrderDetailVO detail(WorkOrderDetailParam param) {
        return null;
    }

    @Override
    public WorkOrderUpdateStatusVO finishWorkOrder(WorkOrderUpdateStatusParam param) {
        return null;
    }

    @Override
    public WorkOrderUpdateStatusVO passWorkOrder(WorkOrderUpdateStatusParam param) {
        return null;
    }

    @Override
    public Object applyHelp(WorkOrderHelpParam param) {
        return null;
    }

    @Override
    public Object urgeOrder(Object workOrderUrgeOrderParam) {
        return null;
    }

    @Override
    public Object deleteOrder(Object param) {
        return null;
    }

    @Override
    public Object cancel(Object param) {
        return null;
    }

    @Override
    public WorkOrderUpdateStatusVO checkWorkOrder(WorkOrderUpdateStatusParam param) {
        return null;
    }
}
