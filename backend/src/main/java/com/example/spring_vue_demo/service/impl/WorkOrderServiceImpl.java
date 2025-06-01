package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.utils.OrderCodeUtils;
import com.example.spring_vue_demo.vo.WorkOrderCreateVO;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Slf4j
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

    @Override
    public Result create(WorkOrderCreateParam param){
        //参数校验
        if(param == null || StringUtils.isBlank(param.getTitle())
            || param.getType()==null ||param.getPriorityLevel()==null)
        {
            return Result.error("信息不完整");
        }
        WorkOrder workOrder = new WorkOrder();
        workOrder.setType(param.getType());
        workOrder.setTitle(param.getTitle());
        workOrder.setContent(param.getContent());
        workOrder.setPriorityLevel(param.getPriorityLevel());
        workOrder.setStatus(100);
        //时间
        workOrder.setCreateTime(1L);
        //删除位

        String orderCode = OrderCodeUtils.generateWorkOrderCode();
        log.info(orderCode);
        workOrder.setCode(orderCode);
        if(param.getAccessoryUrl()!=null)
        {
            workOrder.setAccessoryUrl(param.getAccessoryUrl());
            workOrder.setAccessoryName(param.getAccessoryName());
        }

        boolean isSaved = this.save(workOrder);
        if(isSaved)
        {
            WorkOrderCreateVO workOrderCreateVO  = new WorkOrderCreateVO();
            workOrderCreateVO.setId(workOrder.getId());
            workOrderCreateVO.setCode(workOrder.getCode());
            return Result.success(workOrderCreateVO);
        }
        else
        {
            return Result.error("创建失败");
        }

    }
}
