package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.enums.TimeTypeEnum;
import com.example.spring_vue_demo.enums.WorkOrderStatusEnum;
import com.example.spring_vue_demo.mapper.HandleUserInfoMapper;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.StatusDataParam;
import com.example.spring_vue_demo.param.WeekHandleQuantityParam;
import com.example.spring_vue_demo.service.DashboardService;
import com.example.spring_vue_demo.service.convert.WorkOrderConverter;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.service.query.WorkOrderQuery;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.example.spring_vue_demo.vo.StatusDataVO;
import com.example.spring_vue_demo.vo.WeekHandleVO;
import com.example.spring_vue_demo.vo.WorkOrderDataVO;
import com.example.spring_vue_demo.vo.WorkOrderTodoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceimpl implements DashboardService {
    private final WorkOrderMapper workOrderMapper;
    private final HandleUserInfoMapper handleUserInfoMapper;

    private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public WorkOrderDataVO getData() {
        //所有数据
        Long currentTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        Long monthAgoTime = LocalDateTime.now().minusMonths(1L).atZone(ZoneId.systemDefault()).toEpochSecond();
        LambdaQueryWrapper<WorkOrder> finishWrapper = WorkOrderQuery.getByDateStatus(List.of(WorkOrderStatusEnum.FINISHED.getValue(), WorkOrderStatusEnum.CHECKED.getValue()), currentTime, monthAgoTime);
        Long monthFinishedNum = workOrderMapper.selectCount(finishWrapper);
        LambdaQueryWrapper<WorkOrder> unHandledWrapper = WorkOrderQuery.getByDateStatus(List.of(WorkOrderStatusEnum.HANDLING.getValue(), WorkOrderStatusEnum.DELAYED.getValue()), null, null);
        Long unHandledNum = workOrderMapper.selectCount(unHandledWrapper);
        LambdaQueryWrapper<WorkOrder> unAuditedWrapper = WorkOrderQuery.getByDateStatus(List.of(WorkOrderStatusEnum.UNAUDITED.getValue()), null, null);
        Long unAuditedNum = workOrderMapper.selectCount(unAuditedWrapper);
        LambdaQueryWrapper<WorkOrder> delayWrapper = WorkOrderQuery.getByDateStatus(List.of(WorkOrderStatusEnum.DELAYED.getValue()), null, null);
        Long delayNum = workOrderMapper.selectCount(delayWrapper);
        WorkOrderDataVO vo = new WorkOrderDataVO();
        vo.setMonthFinishedNum(monthFinishedNum);
        vo.setUnHandledNum(unHandledNum);
        vo.setUnAuditedNum(unAuditedNum);
        vo.setDelayNum(delayNum);
        return vo;
    }

    @Override
    public List<WorkOrderTodoVO> getTodo() {
        //查询该用户应审核、应分配、应完成、应确认完成的工单
        Long userId = StaffHolder.get().getId();
        //查询该用户对应的用户信息
        LambdaQueryWrapper<HandleUserInfo> handleUserInfowrapper = HandleUserInfoQuery.getOrderIdByUserIdHandleTypeWrapper(userId, List.of(HandleUserInfoHandleTypeEnum.AUDIT.getValue(), HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue(),
                HandleUserInfoHandleTypeEnum.HANDLE.getValue(), HandleUserInfoHandleTypeEnum.CHECK.getValue()), Boolean.FALSE);
        List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(handleUserInfowrapper);
        List<Long> orderIds = handleUserInfos.stream().map(HandleUserInfo::getOrderId).distinct().toList();
        LambdaQueryWrapper<WorkOrder>workOrderWrapper=WorkOrderQuery.getWorkOrderByIds(orderIds);
        List<WorkOrder> workOrders = workOrderMapper.selectList(workOrderWrapper);
        List<WorkOrderTodoVO> workOrderTodoVOS = WorkOrderConverter.INSTANCE.toWorkTodoVOS(workOrders);
        return workOrderTodoVOS;
    }

    @Override
    public List<StatusDataVO> getStatus(StatusDataParam param) {
        //所有数据
        TimeTypeEnum timeTypeEnum=TimeTypeEnum.getByValue(param.getTimeType());
        Long createTimeFrom=null;
        Long createTimeTo=LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        switch(timeTypeEnum){
            case WEEK -> createTimeFrom=LocalDateTime.now().minusWeeks(1L).atZone(ZoneId.systemDefault()).toEpochSecond();
            case MONTH -> createTimeFrom=LocalDateTime.now().minusMonths(1L).atZone(ZoneId.systemDefault()).toEpochSecond();
            case YEAR -> createTimeFrom=LocalDateTime.now().minusYears(1L).atZone(ZoneId.systemDefault()).toEpochSecond();
        }
        QueryWrapper<WorkOrder> workOrderWrapper=WorkOrderQuery.getCountGroupByStatusByDate(createTimeFrom,createTimeTo);
        List<Map<String,Object>> statusMaps = workOrderMapper.selectMaps(workOrderWrapper);
        List<StatusDataVO>vos= statusMaps.stream().map(map->{
            StatusDataVO vo=new StatusDataVO();
            Integer status=(Integer)map.get("status");
            Long count=(Long)map.get("count");
            vo.setStatus(status);
            vo.setStatusDesc(Objects.requireNonNull(WorkOrderStatusEnum.getByValue(status)).getDesc());
            vo.setQuantity(count);
            return vo;
        }).toList();
        return vos;
    }

    @Override
    public WeekHandleVO getWeekHandleQuantity(WeekHandleQuantityParam param) {
        //取当天起始时间
        LocalDate date = LocalDate.parse(param.getDate(), formatter);
        LocalDateTime startOfDate=date.atStartOfDay();
        LocalDateTime endOfDate=date.plusDays(1L).atStartOfDay();
        Long startTimeFrom=startOfDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        Long startTimeEnd=endOfDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        //查当天所有完成的操作信息
        LambdaQueryWrapper<HandleUserInfo> totalWrapper=HandleUserInfoQuery.getByHandleDateAndHandleType(startTimeFrom,startTimeEnd,List.of(HandleUserInfoHandleTypeEnum.HANDLE.getValue()));
        List<HandleUserInfo>userInfos = handleUserInfoMapper.selectList(totalWrapper);
        if(CollectionUtils.isEmpty(userInfos)){
            return new WeekHandleVO(0L,0L, param.getDate());
        }
        //所有工单
        Long dailyTotalNum=userInfos.stream().map(HandleUserInfo::getOrderId).distinct().count();
        //已完成工单
        Map<Long,List<HandleUserInfo>>collect=userInfos.stream().collect(Collectors.groupingBy(HandleUserInfo::getOrderId));
        Long dailyFinishedNum=0L;
        for(Map.Entry<Long,List<HandleUserInfo>>entry:collect.entrySet()){
            List<HandleUserInfo>handleUserInfos=entry.getValue();
            boolean finished=true;
            for (HandleUserInfo handleUserInfo : handleUserInfos) {
                //如果存在操作信息是未完成
                if(handleUserInfo.getFinished().equals(Boolean.FALSE)){
                    finished=false;
                    break;
                }
            }
            if(finished){
                dailyFinishedNum++;
            }
        }
        WeekHandleVO vo=new WeekHandleVO();
        vo.setDate(param.getDate());
        vo.setDailyTotalNum(dailyTotalNum);
        vo.setDailyFinishedNum(dailyFinishedNum);
        return vo;
    }

    @Override
    public Object getMessages(Object param) {
        return null;
    }
}
