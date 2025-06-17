package com.example.spring_vue_demo.service.helper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.*;
import com.example.spring_vue_demo.enums.*;
import com.example.spring_vue_demo.exception.UserSideException;
import com.example.spring_vue_demo.mapper.*;
import com.example.spring_vue_demo.param.HandleUserInfoParam;
import com.example.spring_vue_demo.param.WorkOrder.WorkOrderApprovalParam;
import com.example.spring_vue_demo.param.WorkOrder.WorkOrderCreateParam;
import com.example.spring_vue_demo.param.WorkOrder.WorkOrderPageParam;
import com.example.spring_vue_demo.service.FlowService;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.utils.OrderCodeUtils;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.example.spring_vue_demo.vo.WorkOrder.WorkOrderPageVO;
import com.example.spring_vue_demo.vo.WorkOrder.WorkOrderUpdateStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final FlowService flowService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void gatherQueryWorkOrderId(List<HandleUserInfo> handleUserInfos, HandleUserInfoParam userInfoParam, HandleUserInfoHandleTypeEnum userInfoType) {
        if (userInfoParam==null) {
            return;
        }
        LambdaQueryWrapper<HandleUserInfo> checkerWrapper = HandleUserInfoQuery.getWorkOrderPageWrapper(userInfoParam, userInfoType.getValue());
        List<HandleUserInfo> submitterInfolist = handleUserInfoMapper.selectList(checkerWrapper);
        handleUserInfos.addAll(submitterInfolist);
    }

    public List<HandleUserInfo> getQueryWorkOrderIds(WorkOrderPageParam param) {
        List<HandleUserInfo> queryHandleUserInfos = new ArrayList<>();
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getSubmitterInfo(), HandleUserInfoHandleTypeEnum.SUBMIT);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getAuditorInfo(), HandleUserInfoHandleTypeEnum.AUDIT);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getDistributerInfo(), HandleUserInfoHandleTypeEnum.DISTRIBUTE);
        gatherQueryWorkOrderId(queryHandleUserInfos, param.getHandlerInfo(), HandleUserInfoHandleTypeEnum.HANDLE);
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
            case AUDIT -> {
                correctStatusEnums.addAll(List.of(WorkOrderStatusEnum.UNAUDITED,WorkOrderStatusEnum.AUDITING));
                break;
            }
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
            case AUDIT -> {
                throw new UserSideException(ErrorCode.AUDIT_ING_STATUS_WRONG);
            }
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

    public List<Message> buildMessages(WorkOrderStatusEnum status, String code, List<Long> assignedUserIds, boolean finished) {
        //todo:消息队列
        String content = "";
        switch (status) {
            case UNDISTRIBUTED -> {
                content = "编号为" + code + "的工单已经审核完毕，需要您派单。";
                break;
            }
            case HANDLING -> {
                content = "您收到编号为" + code + "的工单，请尽快处理。";
                break;
            }
            case DELAYED -> {
                content = "编号为" + code + "的工单已超时，请尽快完成。";
                break;
            }
            case FINISHED -> {
                if (finished) {
                    content = "编号为" + code + "的工单需要您验收。";
                }
                break;
            }
            case CHECKED -> {
                content = "您完成的编号为" + code + "的工单，已经验收成功。";
                break;
            }
            case CHECK_FAILURE -> {
                content = "您完成的编号为" + code + "的工单，验收失败，请查看。";
                break;
            }
        }
        //设置发送人和接收人id
        //系统delay信息默认发送人为0
        Long userId;
        if(StaffHolder.get()!=null) {
            userId = StaffHolder.get().getId();
        } else {
            userId = 0L;
        }
        List<Message> messages = new ArrayList<>();
        String finalContent = content;
        assignedUserIds.forEach(assignedUserId -> {
            Message message = new Message();
            message.setContent(finalContent);
            message.setType(status.getValue());
            message.setTypeDesc(status.getDesc());
            message.setSenderId(userId);
            message.setReceiverId(assignedUserId);
            message.setSendTime(formatter.format(LocalDateTime.now()));
            messages.add(message);
        });
        return messages;
    }
    public void updateNextStatus(HandleTypeEnum handleType, WorkOrder workOrder, boolean finished) {
        //催单和协助帮忙不需要更新状态
        WorkOrderStatusEnum statusEnum = WorkOrderStatusEnum.getByValue(workOrder.getStatus());
        switch (handleType) {
            case CREATED->{
                statusEnum = WorkOrderStatusEnum.UNAUDITED;
            }
            case AUDIT -> {
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
                if (finished) {
                    statusEnum = WorkOrderStatusEnum.FINISHED;
                }
            }
        }
        assert statusEnum != null;
        workOrder.setStatus(statusEnum.getValue());
    }

    public boolean checkAllFinishedBeforeUpdateStatus(List<HandleUserInfo> handleUserInfos) {
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
            if (handleTypeEnum.equals(HandleTypeEnum.DISTRIBUTE)) {
                handleUserInfo.setHandleTime(formatter.format(LocalDateTime.now()));
            }
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
    public void addCheckInfo(Long orderId,Long checkId){
        Staff checker = staffMapper.selectById(checkId);
        HandleUserInfo checkInfo = new HandleUserInfo();
        checkInfo.setOrderId(orderId);
        checkInfo.setUserId(checkId);
        checkInfo.setUserName(checker.getName());
        checkInfo.setCompanyCode(checker.getCompanyCode());
        checkInfo.setCompanyName(checker.getCompany());
        checkInfo.setDepartmentCode(checker.getDepartmentCode());
        checkInfo.setDepartmentName(checker.getDepartment());
        checkInfo.setHandleType(HandleUserInfoHandleTypeEnum.CHECK.getValue());
        checkInfo.setFinished(Boolean.FALSE);
        handleUserInfoMapper.insert(checkInfo);
    }

    public void addDistributeInfo(Long orderId,Long distributeId)
    {
        Staff distributor = staffMapper.selectById(distributeId);
        HandleUserInfo distributeInfo = new HandleUserInfo();
        distributeInfo.setOrderId(orderId);
        distributeInfo.setUserId(distributeId);
        distributeInfo.setUserName(distributor.getName());
        distributeInfo.setCompanyCode(distributor.getCompanyCode());
        distributeInfo.setCompanyName(distributor.getCompany());
        distributeInfo.setDepartmentCode(distributor.getDepartmentCode());
        distributeInfo.setDepartmentName(distributor.getDepartment());
        distributeInfo.setHandleType(HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue());
        distributeInfo.setFinished(Boolean.FALSE);
        handleUserInfoMapper.insert(distributeInfo);

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
        if ((handleType.equals(HandleTypeEnum.URGE_ORDER) || handleType.equals(HandleTypeEnum.CHECK_SUCCESS)
                || handleType.equals(HandleTypeEnum.CHECK_FAILURE)) && assignedUserId != null) {
            throw new UserSideException(ErrorCode.NOT_NEED_ASSIGNED_USER_ID);
        }

    }

    public void updateFinishHandleInfo(WorkOrder workOrder, HandleTypeEnum handleType, String remark) {
        Long orderId = workOrder.getId();
        Long handleTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        if (handleType.equals(HandleTypeEnum.DISTRIBUTE)) {
            //筛选本用户对应状态的操作信息
            Long staffId = StaffHolder.get().getId();
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, List.of(staffId), HandleUserInfoHandleTypeEnum.DISTRIBUTE.getValue(), Boolean.TRUE, handleTime, remark);
            handleUserInfoMapper.update(wrapper);
        } else if (handleType.equals(HandleTypeEnum.FINISH)) {
            //筛选本用户对应状态的操作信息
            Long staffId = StaffHolder.get().getId();
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, List.of(staffId), HandleUserInfoHandleTypeEnum.HANDLE.getValue(), Boolean.TRUE, handleTime, remark);
            handleUserInfoMapper.update(wrapper);
        } else if (handleType.equals(HandleTypeEnum.CHECK_SUCCESS)) {
            //筛选本用户对应状态的操作信息
            Long staffId = StaffHolder.get().getId();
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, List.of(staffId), HandleUserInfoHandleTypeEnum.CHECK.getValue(), Boolean.TRUE, handleTime, remark);
            handleUserInfoMapper.update(wrapper);
        } else if (handleType.equals(HandleTypeEnum.CHECK_FAILURE)) {
            //回退所有处理完成状态的操作信息
            LambdaQueryWrapper<HandleUserInfo> handleTypeWrapper = HandleUserInfoQuery.getHandleTypeWrapper(orderId, HandleUserInfoHandleTypeEnum.HANDLE.getValue());
            List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(handleTypeWrapper);
            List<Long> handleUserIds = handleUserInfos.stream().map(HandleUserInfo::getUserId).collect(Collectors.toList());
            LambdaUpdateWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getUpdateStatusWrapper(orderId, handleUserIds, HandleUserInfoHandleTypeEnum.HANDLE.getValue(), Boolean.FALSE, null,remark);
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
            case AUDIT -> handleTypeInfo = HandleUserInfoHandleTypeEnum.AUDIT;
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

    public List<Long> getReceiverIds(HandleTypeEnum handleType, Long orderId, Long assignedId) {
        if (handleType.equals(HandleTypeEnum.DISTRIBUTE) || handleType.equals(HandleTypeEnum.APPLY_HELP)) {
            return List.of(assignedId);
        }
        HandleUserInfoHandleTypeEnum handleUserInfoType = null;
        if (handleType.equals(HandleTypeEnum.URGE_ORDER) || handleType.equals(HandleTypeEnum.CHECK_SUCCESS)
                || handleType.equals(HandleTypeEnum.CHECK_FAILURE)) {
            //催单、验收成功、验收失败操作，接收人存在类型为完成的操作信息里
            handleUserInfoType = HandleUserInfoHandleTypeEnum.HANDLE;
        } else if (handleType.equals(HandleTypeEnum.FINISH)) {
            //已完成操作，接收人存在类型为检查的操作信息里
            handleUserInfoType = HandleUserInfoHandleTypeEnum.CHECK;
        }
        LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeWrapper(orderId, handleUserInfoType.getValue());
        List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(wrapper);
        List<Long> receiverIds = handleUserInfos.stream().map(HandleUserInfo::getUserId).toList();
        return receiverIds;
    }

    public WorkOrder createWorkOrder(WorkOrderCreateParam param)
    {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setType(param.getType());
        workOrder.setTitle(param.getTitle());
        workOrder.setContent(param.getContent());
        workOrder.setPriorityLevel(param.getPriorityLevel());
        workOrder.setStatus(100); //待审核
        workOrder.setFlowId(param.getFlowId());
        workOrder.setContent(param.getContent());
        //时间
        String formatNow = formatter.format(LocalDateTime.now());
        workOrder.setCreateTime(formatNow);
        workOrder.setUpdateTime(formatNow);
        //转换时间戳
        if(param.getDeadlineTime()!=null) {
            String formattedTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(param.getDeadlineTime()),
                    ZoneId.systemDefault()
            ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info(formattedTime);
            workOrder.setDeadlineTime(formattedTime);
        }
        String orderCode = OrderCodeUtils.generateWorkOrderCode();
        workOrder.setCode(orderCode);
        workOrder.setDeleted(0);
        if (param.getAccessoryUrl() != null) {
            workOrder.setAccessoryUrl(param.getAccessoryUrl());
            workOrder.setAccessoryName(param.getAccessoryName());
        }
        return workOrder;
    }

    public boolean updateHandleUserInfo(WorkOrderApprovalParam param) {
        Staff staff = StaffHolder.get();
        int updateCount = handleUserInfoMapper.update(null,
                new UpdateWrapper<HandleUserInfo>()
                        .eq("order_id", param.getId())
                        .eq("user_id", staff.getId())
                        .eq("handle_type",2)
                        .eq("finished",0)
                        .set("finished", 1)
                        .set("remark", param.getRemark())
        );
        return updateCount == 1;
    }
    //找打第一个审核人
    public Long findAuditId(Long staffId) {
        //第一个审核人是部门主管
        Staff staff = staffMapper.selectOne(
                new QueryWrapper<Staff>().eq("id", staffId)
        );
        if(staff == null){
            return null;
        }
        if(staff.getManagerNumber() == null){
            return staff.getId();
        } else
        {
            Staff manager = staffMapper.selectOne(
                    new QueryWrapper<Staff>().eq("staff_number", staff.getManagerNumber())
            );
            return manager.getId();
        }
    }
    //找到下一个审核的人，找到上级部门的主管
    public Long findNextStaff() {
        Staff nowStaff = StaffHolder.get();
        String departmentCode= departmentMapper.findDepartmentCodeByName(nowStaff.getCompany(),nowStaff.getDepartment());
        if(departmentCode==null){
            return null;
        }
        Long leaderId = departmentMapper.findParentDepartmentLeaderId(departmentCode);
        if (leaderId == null) {
            return null;
        }
        return leaderId;
    }
    //找到分配人
    public Long findDistributeId(Integer type){
        WorkOrderTypeEnum workOrderTypeEnum = WorkOrderTypeEnum.getByValue(type);
        if (workOrderTypeEnum == null) {
            return  null;
        }
        Staff leader = null;
        Department department = null;
        switch (workOrderTypeEnum) {
            case REQUIREMENTS:
                //查询总公司的财务部主管
                department = departmentMapper.selectOne(new LambdaQueryWrapper<Department>()
                        .eq(Department::getName, "财务部")
                        .eq(Department::getCompanyCode, "91440000HBZRRFFRF2"));

                if (department != null && department.getLeaderNumber() != null) {
                    leader = staffMapper.selectOne(new LambdaQueryWrapper<Staff>()
                            .eq(Staff::getStaffNumber, department.getLeaderNumber()));
                }
                break;

            case FAULT:
                //查询总公司的财务部主管
                department = departmentMapper.selectOne(new LambdaQueryWrapper<Department>()
                        .eq(Department::getName, "运维部")
                        .eq(Department::getCompanyCode, "91440000HBZRRFFRF2"));

                if (department != null && department.getLeaderNumber() != null) {
                    leader = staffMapper.selectOne(new LambdaQueryWrapper<Staff>()
                            .eq(Staff::getStaffNumber, department.getLeaderNumber()));
                }
                break;
            default:
                return null;
        }
        return leader.getId();
    }

    public void checkApprovalWorkOrderStatus(WorkOrder workOrder) {
        Integer workOrderStatus = workOrder.getStatus();
        List<WorkOrderStatusEnum> correctStatusEnums = List.of(WorkOrderStatusEnum.UNAUDITED,WorkOrderStatusEnum.AUDITING);
        for (WorkOrderStatusEnum correctWorkOrderStatusEnum : correctStatusEnums) {
            if (workOrderStatus.equals(correctWorkOrderStatusEnum.getValue())) {
                return;
            }
        }
        throw new UserSideException(ErrorCode.UNAUDITED_AUDITING_STATUS_WRONG);
    }

    public void checkHandleUserInfoExist(Long userId, Long orderId) {
        LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeUserIdWrapper(orderId, HandleUserInfoHandleTypeEnum.AUDIT.getValue(), userId);
        HandleUserInfo currentHandleInfo = handleUserInfoMapper.selectOne(wrapper);
        if (currentHandleInfo == null) {
            throw new UserSideException(ErrorCode.CURRENT_USER_IS_NOT_HANDLE_USER);
        }
    }
}
