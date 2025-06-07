package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Message;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.enums.HandleTypeEnum;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.enums.WorkOrderStatusEnum;
import com.example.spring_vue_demo.mapper.HandleUserInfoMapper;
import com.example.spring_vue_demo.mapper.MessageMapper;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderHandleParam;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import com.example.spring_vue_demo.service.MessageService;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.utils.OrderCodeUtils;
import com.example.spring_vue_demo.vo.*;
import com.example.spring_vue_demo.service.convert.WorkOrderConverter;
import com.example.spring_vue_demo.service.helper.WorkOrderHelper;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.service.query.WorkOrderQuery;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.*;

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
    private final MessageMapper messageMapper;

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
        List<WorkOrder> workOrders = workOrderHelper.addPageHandleInfo(pageHandleUserInfos, pageWorkOrders);
        //转换vo
        List<WorkOrderPageVO> workOrderVOS = WorkOrderConverter.INSTANCE.toPageVOS(workOrders);
        //设置分页信息
        IPage<WorkOrderPageVO> workOrderPageVOS = workOrderHelper.setPageInfo(workOrderVOS, pageWorkOrders);
        return workOrderPageVOS;
    }

    @Override
    public WorkOrderDetailVO detail(WorkOrderDetailParam param) {
        //校验id和code不能全为空
        workOrderHelper.checkIdAndCodeNotNull(param.getId(), param.getCode());
        //查询工单主表
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(param.getId(), param.getCode());
        WorkOrder workOrder = getOne(workOrderWrapper);
        //校验工单不为空
        workOrderHelper.checkWorkOrderExist(workOrder);
        //查询操作信息
        LambdaQueryWrapper<HandleUserInfo> handleUserInfoWrapper = HandleUserInfoQuery.getOrderIdWrapper(workOrder.getId());
        List<HandleUserInfo> pageHandleUserInfos = iHandleUserInfoService.list(handleUserInfoWrapper);
        //组装操作信息
        workOrder = workOrderHelper.addDetailHandleInfo(pageHandleUserInfos, workOrder);
        //转换vo
        WorkOrderDetailVO workOrderDetailVO = WorkOrderConverter.INSTANCE.toDetailVO(workOrder);
        return workOrderDetailVO;
    }

    @Override
    @Transactional
    public WorkOrderUpdateStatusVO handleWorkOrder(WorkOrderHandleParam param) {
        HandleTypeEnum handleType = HandleTypeEnum.getByValue(param.getHandleType());
        assert handleType != null;
        //校验工单id和code不能全为空
        workOrderHelper.checkIdAndCodeNotNull(param.getId(), param.getCode());
        //校验分配和协助的用户信息是否填写
        workOrderHelper.checkAssignedUserInfo(handleType, param.getAssignedUserId());
        //查询工单，如果不存在返回错误信息
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(param.getId(), param.getCode());
        WorkOrder workOrder = getOne(workOrderWrapper);
        workOrderHelper.checkWorkOrderExist(workOrder);
        //校验工单状态和操作是否匹配
        workOrderHelper.checkHandleWorkOrderStatus(workOrder, handleType);
        //校验当前用户是否是工单当前状态的操作人
        workOrderHelper.checkWorkOrderUser(workOrder, handleType);
        //校验安排的用户是否已经被添加（接口幂等）
        workOrderHelper.checkAssignedUserInfoExist(workOrder, param.getAssignedUserId(), handleType);
        //添加操作信息
        workOrderHelper.addHandleInfo(workOrder.getId(), handleType, param.getAssignedUserId(), param.getRemark());
        //更新操作信息完成状态和处理时间
        workOrderHelper.updateFinishHandleInfo(workOrder, handleType);
        //更新工单主表状态
        workOrderHelper.updateNextStatus(handleType, workOrder);
        boolean updateSuccess = updateById(workOrder);
        //发送信息
        List<Long> receiverIds = workOrderHelper. getReceiverIds(handleType, workOrder.getId(), param.getAssignedUserId());
        List<Message> messages = workOrderHelper.buildMessages(WorkOrderStatusEnum.getByValue(workOrder.getStatus()), workOrder.getCode(), receiverIds);
        List<BatchResult>  msgSuccess = messageMapper.insert(messages);
        //构建返回VO
        WorkOrderUpdateStatusVO vo = workOrderHelper.setUpdateReturnVO(updateSuccess, workOrder.getCode(), workOrder.getId());
        return vo;
    }



    @Override
    public WorkOrderUpdateStatusVO deleteOrder(WorkOrderDeleteParam param) {
        //校验工单id和code不能全为空
        workOrderHelper.checkIdAndCodeNotNull(param.getId(), param.getCode());
        //查询工单，如果不存在返回错误信息
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(param.getId(), param.getCode());
        WorkOrder workOrder = getOne(workOrderWrapper);
        workOrderHelper.checkWorkOrderExist(workOrder);
        //删除工单
        boolean success = removeById(workOrder.getId());
        WorkOrderUpdateStatusVO vo = workOrderHelper.setUpdateReturnVO(success, workOrder.getCode(), workOrder.getId());
        return vo;
    }

    @Override
    public WorkOrderUpdateStatusVO cancel(WorkOrderCancelParam param) {
        //校验工单id和code不能全为空
        workOrderHelper.checkIdAndCodeNotNull(param.getId(), param.getCode());
        //查询工单，如果不存在返回错误信息
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(param.getId(), param.getCode());
        WorkOrder workOrder = getOne(workOrderWrapper);
        workOrderHelper.checkWorkOrderExist(workOrder);
        //校验工单状态，已完成、已确认完成的工单不能取消
        workOrderHelper.checkCancelWorkOrderStatus(workOrder);
        //校验取消操作用户，提出人才能进行取消操作
        workOrderHelper.checkCancelUser(workOrder);
        //更新工单状态为取消
        workOrder.setStatus(WorkOrderStatusEnum.CANCELLED.getValue());
        boolean success = updateById(workOrder);
        WorkOrderUpdateStatusVO vo = workOrderHelper.setUpdateReturnVO(success, workOrder.getCode(), workOrder.getId());
        return vo;
    }

    @Override
    public Result create(WorkOrderCreateParam param) {
        //参数校验
        if (param == null || StringUtils.isBlank(param.getTitle())
                || param.getType() == null || param.getPriorityLevel() == null) {
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
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        String formatNow = formatter.format(now.atZone(ZoneId.systemDefault()).toInstant());
        log.debug(formatNow);
        workOrder.setCreateTime(formatNow);

        String orderCode = OrderCodeUtils.generateWorkOrderCode();
        log.info(orderCode);
        workOrder.setCode(orderCode);
        workOrder.setDeleted(0);
        if (param.getAccessoryUrl() != null) {
            workOrder.setAccessoryUrl(param.getAccessoryUrl());
            workOrder.setAccessoryName(param.getAccessoryName());
        }

        boolean isSaved = this.save(workOrder);
        if (isSaved) {
            WorkOrderCreateVO workOrderCreateVO = new WorkOrderCreateVO();
            workOrderCreateVO.setId(workOrder.getId());
            workOrderCreateVO.setCode(workOrder.getCode());
            return Result.success(workOrderCreateVO);
        } else {
            return Result.error("创建失败");
        }

    }

    //5min检查一次数据库中延迟的工单
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void checkAndUpdateOverdueOrders() {
        // 查询所有状态不是delay且deadlineTime已过的工单
        LocalDateTime now = LocalDateTime.now();
        Long nowTime = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LambdaQueryWrapper<WorkOrder> getDelayWrapper = HandleUserInfoQuery.getByStatusAndDeadlineTime(nowTime, WorkOrderStatusEnum.DELAYED.getValue());
        List<WorkOrder> overdueOrders = this.list(getDelayWrapper);
        for (WorkOrder order : overdueOrders) {
            //更新工单状态
            LambdaUpdateWrapper<WorkOrder> updateWrapper = WorkOrderQuery.getUpdateStatusByIdWrapper(order.getId(), WorkOrderStatusEnum.DELAYED.getValue());
            update(updateWrapper);
            // 查工单的处理人
            List<Long> handleUserIds = workOrderHelper.getHandleUserIds(order.getId());
            // 发送通知给用户
            List<Message> messages = workOrderHelper.buildMessages(WorkOrderStatusEnum.DELAYED, order.getCode(), handleUserIds);
            List<BatchResult> success = messageMapper.insert(messages);
        }
        log.info("已完成一次延期工单扫描");
    }

}
