package com.example.spring_vue_demo.service.convert;


import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.vo.HandleUserInfoVO;
import com.example.spring_vue_demo.vo.WorkOrderDetailVO;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WorkOrderConverter {
    WorkOrderConverter INSTANCE=  Mappers.getMapper(WorkOrderConverter.class);

    List<WorkOrderPageVO> toPageVOS(List<WorkOrder> workOrders);

    @Mapping(target="priorityLevelDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderPriorityLevelEnum.getByValue(workOrder.getPriorityLevel()).getDesc() )")
    @Mapping(target="statusDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderStatusEnum.getByValue(workOrder.getStatus()).getDesc() )")
    @Mapping(target="typeDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderTypeEnum.getByValue(workOrder.getType()).getDesc() )")
    WorkOrderPageVO toPageVO(WorkOrder workOrder);

    List<HandleUserInfoVO> toVOS(List<HandleUserInfo> pageHandleUserInfos);

    @Mapping(target="handleTypeDesc",expression = "java( com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum.getByValue(pageHandleUserInfo.getHandleType()).getDesc() )")
    HandleUserInfoVO toVO(HandleUserInfo pageHandleUserInfo);

    @Mapping(target="priorityLevelDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderPriorityLevelEnum.getByValue(workOrder.getPriorityLevel()).getDesc() )")
    @Mapping(target="statusDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderStatusEnum.getByValue(workOrder.getStatus()).getDesc() )")
    @Mapping(target="typeDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderTypeEnum.getByValue(workOrder.getType()).getDesc() )")
    WorkOrderDetailVO toDetailVO(WorkOrder workOrder);
}
