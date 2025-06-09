package com.example.spring_vue_demo.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.*;
import com.example.spring_vue_demo.enums.ErrorCode;
import com.example.spring_vue_demo.enums.HandleTypeEnum;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.enums.WorkOrderStatusEnum;
import com.example.spring_vue_demo.exception.UserSideException;
import com.example.spring_vue_demo.mapper.CompanyMapper;
import com.example.spring_vue_demo.mapper.DepartmentMapper;
import com.example.spring_vue_demo.mapper.HandleUserInfoMapper;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.param.HandleUserInfoParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.service.query.WorkOrderQuery;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.example.spring_vue_demo.vo.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrderUpdateStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkOrderHelper {
    private final HandleUserInfoMapper handleUserInfoMapper;
    private final StaffMapper staffMapper;
    private final CompanyMapper companyMapper;
    private final DepartmentMapper departmentMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void gatherQueryWorkOrderId(List<HandleUserInfo> handleUserInfos, List<HandleUserInfoParam> userInfoParams, HandleUserInfoHandleTypeEnum userInfoType) {
        if (CollectionUtils.isEmpty(userInfoParams)) {
            return;
        }
        LambdaQueryWrapper<HandleUserInfo> checkerWrapper = HandleUserInfoQuery.getWorkOrderPageWrapper(userInfoParams, userInfoType.getValue());
        List<HandleUserInfo> submitterInfolist = handleUserInfoMapper.selectList(checkerWrapper);
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

    public void checkHandleWorkOrderStatus(WorkOrder workOrder, HandleTypeEnum handleType) {
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

    public Message buildMessage(WorkOrderStatusEnum status, String code, Long assignedUserId) {
        //todo:消息队列
        Message message = new Message();
        String nextHandleType = "";
        switch (status) {
            case UNAUDITED->{
                message.setContent(("编号为"+code+"的工单已成功创建，等待审核"));
                break;
            }
            case AUDITING->{
                message.setContent(("编号为"+code+"的工单需要您审核"));
                break;
            }
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
        //设置发送人和接收人id
        Long userId = StaffHolder.get().getId();
        message.setType(status.getValue());
        message.setTypeDesc(status.getDesc());
        message.setSenderId(userId);
        message.setReceiverId(assignedUserId);
        message.setSendTime(formatter.format(LocalDateTime.now()));
        return message;
    }

    public void updateNextStatus(HandleTypeEnum handleType, WorkOrder workOrder) {
        //催单和协助帮忙不需要更新状态
        WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.getByValue(workOrder.getStatus());
        switch (handleType) {
            case CREATED->{
                statusEnum = WorkOrderStatusEnum.AUDITING;
            }
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
                LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeWrapper(workOrder.getId(), HandleUserInfoHandleTypeEnum.HANDLE.getValue());
                List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(wrapper);
                boolean finished = checkAllFinishedBeforeUpdateStatus(handleUserInfos);
                if (finished) {
                    statusEnum = WorkOrderStatusEnum.FINISHED;
                }
            }
        }
        assert statusEnum != null;
        workOrder.setStatus(statusEnum.getValue());
    }

    private boolean checkAllFinishedBeforeUpdateStatus(List<HandleUserInfo> handleUserInfos) {
        for (HandleUserInfo handleUserInfo : handleUserInfos) {
            if (handleUserInfo.getFinished() == Boolean.FALSE) {
                return false;
            }
        }
        return true;
    }

    public void addHandleInfo(Long orderId, HandleTypeEnum handleTypeEnum, Long assignedUserId, String remark) {
        if (handleTypeEnum.equals(HandleTypeEnum.APPLY_HELP) || handleTypeEnum.equals(HandleTypeEnum.DISTRIBUTE)) {
            HandleUserInfo handleUserInfo = new HandleUserInfo();
            handleUserInfo.setUserId(assignedUserId);
            //查询被分配人的信息并填充
            Staff staff = staffMapper.selectById(assignedUserId);
            handleUserInfo.setUserName(staff.getName());
            handleUserInfo.setCompanyCode(staff.getCompanyCode());
            handleUserInfo.setCompanyName(staff.getCompany());
            handleUserInfo.setDepartmentCode(staff.getDepartmentCode());
            handleUserInfo.setDepartmentName(staff.getDepartment());
            handleUserInfo.setOrderId(orderId);
            handleUserInfo.setRemark(remark);
            handleUserInfo.setFinished(Boolean.FALSE);
            switch (handleTypeEnum) {
                case DISTRIBUTE -> handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.HANDLE.getValue());
                case APPLY_HELP -> handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.HANDLE.getValue());
            }
            handleUserInfo.setFinished(Boolean.FALSE);
            handleUserInfo.setHandleTime(formatter.format(LocalDateTime.now()));
            handleUserInfoMapper.insert(handleUserInfo);
        }
        else if(handleTypeEnum.equals(HandleTypeEnum.CREATED))
        {
            HandleUserInfo handleUserInfo = new HandleUserInfo();
            Staff staff = StaffHolder.get();//获取当前用户
            log.debug(staff.toString());
            handleUserInfo.setUserName(staff.getName());
            handleUserInfo.setCompanyName(staff.getCompany());
            Company company = companyMapper.selectOne(
                    new QueryWrapper<Company>().eq("name", staff.getCompany()));
            handleUserInfo.setCompanyCode(company.getCode());
            handleUserInfo.setDepartmentName(staff.getDepartment());
            Department department = departmentMapper.selectOne(
                    new QueryWrapper<Department>().eq("name", staff.getDepartment())
                            .eq("company_code", company.getCode())
            );
            handleUserInfo.setDepartmentCode(department.getCode());
            handleUserInfo.setOrderId(orderId);
            handleUserInfo.setUserId(staff.getId());
            handleUserInfo.setRemark(remark);
            handleUserInfo.setFinished(Boolean.FALSE);
            handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.SUBMIT.getValue());
            handleUserInfo.setFinished(Boolean.TRUE);
            handleUserInfo.setCreateTime(formatter.format(LocalDateTime.now()));
            handleUserInfo.setHandleTime(formatter.format(LocalDateTime.now()));
            handleUserInfo.setUpdateTime(formatter.format(LocalDateTime.now()));

            handleUserInfoMapper.insert(handleUserInfo);
        }else if(handleTypeEnum.equals(HandleTypeEnum.AUDIT)){
            HandleUserInfo handleUserInfo = new HandleUserInfo();
            handleUserInfo.setUserId(assignedUserId);
            //查询被分配人的信息并填充
            Staff staff = staffMapper.selectById(assignedUserId);
            handleUserInfo.setUserName(staff.getName());
            handleUserInfo.setCompanyCode(staff.getCompanyCode());
            handleUserInfo.setCompanyName(staff.getCompany());
            handleUserInfo.setDepartmentCode(staff.getDepartmentCode());
            handleUserInfo.setDepartmentName(staff.getDepartment());
            handleUserInfo.setOrderId(orderId);
            handleUserInfo.setFinished(Boolean.FALSE);
            handleUserInfo.setHandleType(HandleUserInfoHandleTypeEnum.AUDIT.getValue());
            handleUserInfo.setFinished(Boolean.FALSE);
            handleUserInfo.setHandleTime(formatter.format(LocalDateTime.now()));
            handleUserInfo.setUpdateTime(formatter.format(LocalDateTime.now()));
            handleUserInfoMapper.insert(handleUserInfo);
        }
    }

    public void checkAssignedUserInfo(HandleTypeEnum handleType, Long assignedUserId) {
        if (handleType.equals(HandleTypeEnum.DISTRIBUTE) && assignedUserId == null) {
            throw new UserSideException(ErrorCode.DISTRIBUTE_REQUIRE_ASSIGNED_USER_ID);
        }
        if (handleType.equals(HandleTypeEnum.APPLY_HELP)) {
            if (assignedUserId == null) {
                throw new UserSideException(ErrorCode.HELP_REQUIRE_ASSIGNED_USER_ID);
            }
            Long userId = StaffHolder.get().getId();
            if (userId.equals(assignedUserId)) {
                throw new UserSideException(ErrorCode.ASSIGNED_USER_EQUALS_CURRENT_USER);
            }
        }
        if (handleType.equals(HandleTypeEnum.URGE_ORDER) || handleType.equals(HandleTypeEnum.CHECK_SUCCESS)
                || handleType.equals(HandleTypeEnum.CHECK_FAILURE)) {
            throw new UserSideException(ErrorCode.NOT_NEED_ASSIGNED_USER_ID);
        }

    }

    public void updateFinishHandleInfo(WorkOrder workOrder, HandleTypeEnum handleType) {
        Long orderId = workOrder.getId();
        Long handleTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        if (handleType.equals(HandleTypeEnum.DISTRIBUTE)) {
            //筛选本用户对应状态的操作信息
            Long staffId = StaffHolder.get().getId();
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, List.of(staffId), HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue(), Boolean.TRUE, handleTime);
            handleUserInfoMapper.update(wrapper);
        } else if (handleType.equals(HandleTypeEnum.FINISH) || handleType.equals(HandleTypeEnum.CHECK_SUCCESS)) {
            //筛选本用户对应状态的操作信息
            Long staffId = StaffHolder.get().getId();
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, List.of(staffId), HandleUserInfoHandleTypeEnum.HANDLE.getValue(), Boolean.TRUE, handleTime);
            handleUserInfoMapper.update(wrapper);
        } else if (handleType.equals(HandleTypeEnum.CHECK_FAILURE)) {
            //回退所有处理完成状态的操作信息
            LambdaQueryWrapper<HandleUserInfo> handleTypeWrapper = HandleUserInfoQuery.getHandleTypeWrapper(orderId, HandleUserInfoHandleTypeEnum.HANDLE.getValue());
            List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(handleTypeWrapper);
            List<Long> handleUserIds = handleUserInfos.stream().map(HandleUserInfo::getUserId).collect(Collectors.toList());
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, handleUserIds, HandleUserInfoHandleTypeEnum.HANDLE.getValue(), Boolean.FALSE, null);
            handleUserInfoMapper.update(wrapper);
        }
    }

    public void checkAssignedUserInfoExist(WorkOrder workOrder, Long assignedUserId, HandleTypeEnum handleType) {
        if (assignedUserId != null && (handleType.equals(HandleTypeEnum.DISTRIBUTE) || handleType.equals(HandleTypeEnum.APPLY_HELP))) {
            HandleUserInfoHandleTypeEnum handleUserInfoHandleType = HandleUserInfoHandleTypeEnum.HANDLE;
            LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeUserIdWrapper(workOrder.getId(), handleUserInfoHandleType.getValue(), assignedUserId);
            List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(wrapper);
            if (CollectionUtils.isNotEmpty(handleUserInfos)) {
                throw new UserSideException(ErrorCode.ASSIGNED_USER_ALREADY_EXIST);
            }
        }
    }

    public WorkOrderUpdateStatusVO setUpdateReturnVO(boolean flag, String code, Long id) {
        WorkOrderUpdateStatusVO vo = new WorkOrderUpdateStatusVO();
        vo.setSuccess(flag);
        vo.setCode(code);
        vo.setId(id);
        return vo;
    }

    public void checkWorkOrderUser(WorkOrder workOrder, HandleTypeEnum handleType) {
        Long staffId = StaffHolder.get().getId();
        HandleUserInfoHandleTypeEnum handleTypeInfo = null;
        switch (handleType) {
            case DISTRIBUTE -> handleTypeInfo = HandleUserInfoHandleTypeEnum.DISTRIBUTE;
            case APPLY_HELP, URGE_ORDER, FINISH -> handleTypeInfo = HandleUserInfoHandleTypeEnum.HANDLE;
            case CHECK_SUCCESS, CHECK_FAILURE -> handleTypeInfo = HandleUserInfoHandleTypeEnum.CHECK;
        }
        LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeUserIdWrapper(workOrder.getId(), handleTypeInfo.getValue(), staffId);
        HandleUserInfo currentHandleInfo = handleUserInfoMapper.selectOne(wrapper);
        if (currentHandleInfo == null) {
            throw new UserSideException(ErrorCode.CURRENT_USER_IS_NOT_HANDLE_USER);
        }

    }


    public void checkCancelWorkOrderStatus(WorkOrder workOrder) {
        if (workOrder.getStatus() >= WorkOrderStatusEnum.FINISHED.getValue()) {
            throw new UserSideException(ErrorCode.AFTER_FINISHED_NOT_ALLOW_CANCELLED);
        }
    }

    public void checkCancelUser(WorkOrder workOrder) {
        Long cancelUserId = StaffHolder.get().getId();
        LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeWrapper(workOrder.getId(), HandleUserInfoHandleTypeEnum.SUBMIT.getValue());
        HandleUserInfo submitInfo = handleUserInfoMapper.selectOne(wrapper);
        if (submitInfo == null || !submitInfo.getUserId().equals(cancelUserId)) {
            throw new UserSideException(ErrorCode.ONLY_SUBMIT_USER_CAN_CANCEL_WORK_ORDER);
        }
    }

    public List<Long> getHandleUserIds(Long orderId) {
        LambdaQueryWrapper<HandleUserInfo> handleUserInfoWrapper = HandleUserInfoQuery.getHandleTypeWrapper(orderId, WorkOrderStatusEnum.DELAYED.getValue());
        List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(handleUserInfoWrapper);
        List<Long> handleOrderIds = handleUserInfos.stream().map(HandleUserInfo::getOrderId).toList();
        return handleOrderIds;
    }

    public List<Message> buildMessages(WorkOrderStatusEnum workOrderStatusEnum, String code, List<Long> handleUserIds) {
        List<Message> messages = new ArrayList<>();
        handleUserIds.forEach(handleUserId -> {
                    messages.add(buildMessage(workOrderStatusEnum, code, handleUserId));
                }
        );
        return messages;
    }
}
