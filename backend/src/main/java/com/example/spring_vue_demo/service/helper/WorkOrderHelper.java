package com.example.spring_vue_demo.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.Message;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.enums.ErrorCode;
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
    private final HandleUserInfoService iHandleUserInfoService;

    private void gatherQueryWorkOrderId(List<HandleUserInfo> handleUserInfos, List<HandleUserInfoParam> userInfoParams, HandleUserInfoHandleTypeEnum userInfoType) {
        if (CollectionUtils.isEmpty(userInfoParams)) {
            return;
        }
        LambdaQueryWrapper<HandleUserInfo> checkerWrapper = HandleUserInfoQuery.getQueryHandleUserInfoWrapper(userInfoParams, userInfoType.getValue());
        List<HandleUserInfo> submitterInfolist = iHandleUserInfoService.list(checkerWrapper);
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

    public WorkOrder addDetailHandleInfo(List<HandleUserInfo>  handleUserInfos, WorkOrder workOrder) {
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

    public void checkWorkOrderExist(WorkOrder workOrder){
        if(workOrder==null){
            throw new UserSideException(ErrorCode.WORK_ORDER_NOT_EXIST);
        }
    }

    public boolean checkWorkOrderStatus(WorkOrder workOrder, List<WorkOrderStatusEnum> correctStatusEnums) {
        Integer workOrderStatus= workOrder.getStatus();
        for(WorkOrderStatusEnum correctWorkOrderStatusEnum:correctStatusEnums){
            if(workOrderStatus.equals(correctWorkOrderStatusEnum.getValue())){
                return true;
            }
        }
        return false;
    }

    public Message buildMessage(HandleUserInfoHandleTypeEnum typeEnum,String code) {
        Message message=new Message();
        String content="您收到编号为"+code+"的工单,"+"请尽快"+typeEnum.getDesc();
        message.setContent(content);
        //todo:设置发送人和接收人id
        message.setType(typeEnum.getValue());
        message.setSenderId(1L);
        message.setReceiverId(2L);
        return message;
    }
}
