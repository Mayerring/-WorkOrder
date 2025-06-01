package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderHelpParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderUpdateStatusParam;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.utils.OrderCodeUtils;
import com.example.spring_vue_demo.vo.WorkOrderCreateVO;
import com.example.spring_vue_demo.service.convert.WorkOrderConverter;
import com.example.spring_vue_demo.service.helper.WorkOrderHelper;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.service.query.WorkOrderQuery;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.annotation.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    private final WorkOrderHelper workOrderHelper;
    private final HandleUserInfoService iHandleUserInfoService;


    @Override
    public IPage<WorkOrderPageVO> pageWorkOrder(WorkOrderPageParam param) {
        //查询操作信息对应orderIds，or关系
        List<HandleUserInfo> queryHandleUserInfos = workOrderHelper.getQueryWorkOrderIds(param);
        List<Long> queryOrderIds = queryHandleUserInfos.stream().map(HandleUserInfo::getOrderId).distinct().toList();
        //查询工单主表
        LambdaQueryWrapper<WorkOrder> workWrapper = WorkOrderQuery.getPageWorkWrapper(param, queryOrderIds);
        Page<WorkOrder> pageWrapper = WorkOrderQuery.getPageInfoWrapper(param);
        IPage<WorkOrder> pageWorkOrders = page(pageWrapper, workWrapper);
        //查询操作信息
        List<Long> orderIds = pageWorkOrders.getRecords().stream().map(WorkOrder::getId).toList();
        LambdaQueryWrapper<HandleUserInfo> handleUserInfoWrapper = HandleUserInfoQuery.getPageHandleUserInfoWrapper(orderIds);
        List<HandleUserInfo> pageHandleUserInfos = iHandleUserInfoService.list(handleUserInfoWrapper);
        //组装操作信息
        List<WorkOrder> workOrders = workOrderHelper.addHandleInfo(pageHandleUserInfos, pageWorkOrders);
        //转换vo
        List<WorkOrderPageVO> workOrderVOS = WorkOrderConverter.INSTANCE.toPageVOS(workOrders);
        //设置分页信息
        IPage<WorkOrderPageVO> workOrderPageVOS = workOrderHelper.setPageInfo(workOrderVOS, pageWorkOrders);
        return workOrderPageVOS;
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
    public Object distribute(Object param) {
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
        workOrder.setContent(param.getContent());
        //时间
        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        String formatNow = formatter.format(now.atZone(ZoneId.systemDefault()).toInstant());
        log.debug(formatNow);
        workOrder.setCreateTime(formatNow);

        String orderCode = OrderCodeUtils.generateWorkOrderCode();
        log.info(orderCode);
        workOrder.setCode(orderCode);
        workOrder.setDeleted(0);
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
