package com.example.spring_vue_demo.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.Message;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.enums.ErrorCode;
import com.example.spring_vue_demo.enums.HandleTypeEnum;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.enums.WorkOrderStatusEnum;
import com.example.spring_vue_demo.exception.UserSideException;
import com.example.spring_vue_demo.param.HandleUserInfoParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @date 2025/05/31
 */
@Component
@RequiredArgsConstructor
public class WorkOrderHelper {
    private final HandleUserInfoService handleUserInfoService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private void gatherQueryWorkOrderId(List<HandleUserInfo> handleUserInfos, List<HandleUserInfoParam> userInfoParams, HandleUserInfoHandleTypeEnum userInfoType) {
        if (CollectionUtils.isEmpty(userInfoParams)) {
            return;
        }
        LambdaQueryWrapper<HandleUserInfo> checkerWrapper = HandleUserInfoQuery.getQueryHandleUserInfoWrapper(userInfoParams, userInfoType.getValue());
        List<HandleUserInfo> submitterInfolist = handleUserInfoService.list(checkerWrapper);
        handleUserInfos.addAll(submitterInfolist);
    }

    public List<HandleUserInfo> getQueryWorkOrderIds(WorkOrderPageParam param) {
        List<HandleUserInfo> queryHandleUserInfos = new ArrayList<>();
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getSubmitterInfo(), HandleUserInfoHandleTypeEnum.SUBMIT);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getAuditorInfo(), HandleUserInfoHandleTypeEnum.AUDIT);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getDistributerInfo(), HandleUserInfoHandleTypeEnum.DISTRIBUTE);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getHandleInfo(), HandleUserInfoHandleTypeEnum.HANDLE);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getCheckerInfo(), HandleUserInfoHandleTypeEnum.CHECK);
        return queryHandleUserInfos;
    }


    public List<WorkOrder> addPageHandleInfo(List<HandleUserInfo> pageHandleUserInfos, IPage<WorkOrder> pageWorkOrders) {
        Map<Long, List<HandleUserInfo>> handleUserInfoMap = pageHandleUserInfos.stream().collect(Collectors.groupingBy(HandleUserInfo::getOrderId));
        List<WorkOrder> workOrders = pageWorkOrders.getRecords();
        for (WorkOrder workOrder : workOrders) {
            List<HandleUserInfo> handleUserInfos = handleUserInfoMap.get(workOrder.getId());
            if (CollectionUtils.isEmpty(handleUserInfos)) {
                continue;
            }
            workOrder.setSubmitterInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.SUBMIT.getValue())).findFirst().orElse(null));
            workOrder.setAuditorInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.AUDIT.getValue())).collect(Collectors.toList()));
            workOrder.setDistributerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue())).findFirst().orElse(null));
            workOrder.setHandlerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.HANDLE.getValue())).collect(Collectors.toList()));
            workOrder.setCheckerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.CHECK.getValue())).findFirst().orElse(null));
        }
        return workOrders;
    }

    public IPage<WorkOrderPageVO> setPageInfo(List<WorkOrderPageVO> workOrderVOS, IPage<WorkOrder> pageWorkOrders) {
        IPage<WorkOrderPageVO> workOrderPageVOS = new Page<>();
        workOrderPageVOS.setRecords(workOrderVOS);
        workOrderPageVOS.setPages(pageWorkOrders.getPages());
        workOrderPageVOS.setCurrent(pageWorkOrders.getCurrent());
        workOrderPageVOS.setSize(pageWorkOrders.getSize());
        workOrderPageVOS.setTotal(pageWorkOrders.getTotal());
        return workOrderPageVOS;
    }

    public WorkOrder addDetailHandleInfo(List<HandleUserInfo> handleUserInfos, WorkOrder workOrder) {
        if (CollectionUtils.isEmpty(handleUserInfos)) {
            return workOrder;
        }
        workOrder.setSubmitterInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.SUBMIT.getValue())).findFirst().orElse(null));
        workOrder.setAuditorInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.AUDIT.getValue())).collect(Collectors.toList()));
        workOrder.setDistributerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue())).findFirst().orElse(null));
        workOrder.setHandlerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.HANDLE.getValue())).collect(Collectors.toList()));
        workOrder.setCheckerInfo(handleUserInfos.stream().filter(handleUserInfo -> Objects.equals(handleUserInfo.getHandleType(), HandleUserInfoHandleTypeEnum.CHECK.getValue())).findFirst().orElse(null));
        return workOrder;
    }

    public void checkIdAndCodeNotNull(Long id, String code) {
        if (id == null && code == null) {
            throw new UserSideException(ErrorCode.ID_AND_CODE_IS_NULL);
        }
    }

    public void checkWorkOrderExist(WorkOrder workOrder) {
        if (workOrder == null) {
            throw new UserSideException(ErrorCode.WORK_ORDER_NOT_EXIST);
        }
    }

    public void checkWorkOrderStatus(WorkOrder workOrder, HandleTypeEnum handleType) {
        Integer workOrderStatus = workOrder.getStatus();
        List<WorkOrderStatusEnum> correctStatusEnums = new ArrayList<>();
        switch (handleType) {
            case DISTRIBUTE -> {
                correctStatusEnums.add(WorkOrderStatusEnum.UNDISTRIBUTED);
                break;
            }
            case APPLY_HELP, URGE_ORDER, FINISH -> {
                correctStatusEnums.addAll(List.of(WorkOrderStatusEnum.HANDLING, WorkOrderStatusEnum.DELAYED, WorkOrderStatusEnum.CHECK_FAILURE));
                break;
            }
            case CHECK_SUCCESS, CHECK_FAILURE -> {
                correctStatusEnums.add(WorkOrderStatusEnum.FINISHED);
                break;
            }
        }
        for (WorkOrderStatusEnum correctWorkOrderStatusEnum : correctStatusEnums) {
            if (workOrderStatus.equals(correctWorkOrderStatusEnum.getValue())) {
                return;
            }
        }
        switch (handleType) {
            case DISTRIBUTE -> {
                throw new UserSideException(ErrorCode.DISTRIBUTE_UNDISTRIBUTED_STATUS_WRONG);
            }
            case APPLY_HELP -> {
                throw new UserSideException(ErrorCode.HELP_ING_STATUS_WRONG);
            }
            case URGE_ORDER -> {
                throw new UserSideException(ErrorCode.URGE_ING_STATUS_WRONG);
            }
            case FINISH -> {
                throw new UserSideException(ErrorCode.FINISH_ING_STATUS_WRONG);
            }
            case CHECK_SUCCESS, CHECK_FAILURE -> {
                throw new UserSideException(ErrorCode.CHECK_FINISHED_STATUS_WRONG);
            }
        }
    }

    public Message buildMessage(WorkOrderStatusEnum status, String code) {
        //todo:消息队列
        Message message = new Message();
        String nextHandleType = "";
        switch (status) {
            case UNDISTRIBUTED -> {
                message.setContent("编号为" + code + "的工单已经审核完毕，需要您派单。");
                break;
            }
            case HANDLING -> {
                message.setContent("您收到编号为" + code + "的工单，请尽快处理。");
                break;
            }
            case DELAYED -> {
                message.setContent("编号为" + code + "的工单已超时，请尽快完成。");
                break;
            }
            case FINISHED -> {
                message.setContent("编号为" + code + "的工单需要您验收。");
                break;
            }
            case CHECKED -> {
                message.setContent("您完成的编号为" + code + "的工单，已经验收成功。");
                break;
            }
            case CHECK_FAILURE -> {
                message.setContent("您完成的编号为" + code + "的工单，验收失败，请查看。");
                break;
            }
        }
        //todo:设置发送人和接收人id
        message.setType(status.getValue());
        message.setTypeDesc(status.getDesc());
        message.setSenderId(1L);
        message.setReceiverId(2L);
        message.setSendTime(formatter.format(LocalDateTime.now()));
        return message;
    }

    public void updateNextStatus(HandleTypeEnum handleType, WorkOrder workOrder) {
        //催单和协助帮忙不需要更新状态
        WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.getByValue(workOrder.getStatus());
        switch (handleType) {
            case DISTRIBUTE -> {
                statusEnum = WorkOrderStatusEnum.HANDLING;
            }
            case CHECK_SUCCESS -> {
                statusEnum = WorkOrderStatusEnum.CHECKED;
            }
            case CHECK_FAILURE -> {
                statusEnum = WorkOrderStatusEnum.CHECK_FAILURE;
            }
            case FINISH -> {
                LambdaQueryWrapper<HandleUserInfo>wrapper=HandleUserInfoQuery.getDetailHandleUserInfoWrapper(workOrder.getId());
                List<HandleUserInfo> handleUserInfos = handleUserInfoService.list(wrapper);
                boolean finished=checkAllFinishedBeforeUpdateStatus(handleUserInfos);
                if(finished) {
                    statusEnum = WorkOrderStatusEnum.FINISHED;
                }
            }
        }
        assert statusEnum != null;
        workOrder.setStatus(statusEnum.getValue());
    }

    private boolean checkAllFinishedBeforeUpdateStatus(List<HandleUserInfo> handleUserInfos) {
        for(HandleUserInfo handleUserInfo:handleUserInfos){
            if(handleUserInfo.getFinished()==Boolean.FALSE){
                return false;
            }
        }
        return true;
    }

    public void addHandleInfo(Long orderId, HandleTypeEnum handleTypeEnum, Long assignedUserId) {
        if (handleTypeEnum.equals(HandleTypeEnum.APPLY_HELP) || handleTypeEnum.equals(HandleTypeEnum.DISTRIBUTE)) {
            HandleUserInfo handleUserInfo = new HandleUserInfo();
            handleUserInfo.setUserId(assignedUserId);
            //todo：查询被分配人的信息并填充
            handleUserInfo.setUserName("");
            handleUserInfo.setCompanyId(0L);
            handleUserInfo.setCompanyName("");
            handleUserInfo.setDepartmentId(0L);
            handleUserInfo.setDepartmentName("");
            handleUserInfo.setOrderId(orderId);
            switch (handleTypeEnum) {
                case DISTRIBUTE -> handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.HANDLE.getValue());
                case APPLY_HELP -> handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.HANDLE.getValue());
            }
            handleUserInfo.setFinished(Boolean.FALSE);
            handleUserInfo.setHandleTime(formatter.format(LocalDateTime.now()));
            handleUserInfoService.save(handleUserInfo);
        }
    }

    public void checkAssignedUserInfo(HandleTypeEnum handleType, Long assignedUserId) {
        if (handleType.equals(HandleTypeEnum.DISTRIBUTE) && assignedUserId == null) {
            throw new UserSideException(ErrorCode.DISTRIBUTE_REQUIRE_ASSIGNED_USER_ID);
        }
        if (handleType.equals(HandleTypeEnum.APPLY_HELP) && assignedUserId == null) {
            throw new UserSideException(ErrorCode.HELP_REQUIRE_ASSIGNED_USER_ID);
        }
    }

    public void updateFinishHandleInfo(Long orderId, HandleTypeEnum handleType) {
        if (handleType.equals(HandleTypeEnum.FINISH) || handleType.equals(HandleTypeEnum.DISTRIBUTE) || handleType.equals(HandleTypeEnum.CHECK_SUCCESS)) {
            //todo：取本用户，筛选本用户对应的操作信息
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId);
            handleUserInfoService.update(wrapper);
        }
    }
}
