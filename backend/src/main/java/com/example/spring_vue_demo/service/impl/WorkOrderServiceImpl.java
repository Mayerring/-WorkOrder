package com.example.spring_vue_demo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.*;
import com.example.spring_vue_demo.enums.HandleTypeEnum;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.enums.WorkOrderStatusEnum;
import com.example.spring_vue_demo.mapper.HandleUserInfoMapper;
import com.example.spring_vue_demo.mapper.MessageMapper;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.*;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import com.example.spring_vue_demo.param.WorkOrderHandleParam;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import com.example.spring_vue_demo.service.WorkOrderService;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.example.spring_vue_demo.vo.*;
import com.example.spring_vue_demo.service.convert.WorkOrderConverter;
import com.example.spring_vue_demo.service.helper.WorkOrderHelper;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import com.example.spring_vue_demo.service.query.WorkOrderQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.spring_vue_demo.enums.WorkOrderStatusEnum.AUDITING;

/**
 * @author wtt
 * @date 2025/05/24
 */

/**
 * @author WangDayu
 * @date 2025/6/9
 */

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    private final WorkOrderHelper workOrderHelper;
    private final HandleUserInfoService iHandleUserInfoService;
    private final MessageMapper messageMapper;
    private final StaffMapper staffMapper;
    private final HandleUserInfoMapper handleUserInfoMapper;

    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        //todo:用户改成批量请求协助
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
        //更新操作信息完成状态、处理时间、备注
        workOrderHelper.updateFinishHandleInfo(workOrder, handleType, param.getRemark());
        //已完成状态需要所有处理人都完成才更新工单状态、发送已完成信息
        boolean finished = true;
        if (handleType.equals(HandleTypeEnum.FINISH)) {
            LambdaQueryWrapper<HandleUserInfo> wrapper = HandleUserInfoQuery.getHandleTypeWrapper(workOrder.getId(), HandleUserInfoHandleTypeEnum.HANDLE.getValue());
            List<HandleUserInfo> handleUserInfos = handleUserInfoMapper.selectList(wrapper);
            finished = workOrderHelper.checkAllFinishedBeforeUpdateStatus(handleUserInfos);
        }
        //更新工单主表状态
        workOrderHelper.updateNextStatus(handleType, workOrder, finished);
        boolean updateSuccess = updateById(workOrder);
        //发送信息
        List<Long> receiverIds = workOrderHelper.getReceiverIds(handleType, workOrder.getId(), param.getAssignedUserId());
        List<Message> messages = workOrderHelper.buildMessages(WorkOrderStatusEnum.getByValue(workOrder.getStatus()), workOrder.getCode(), receiverIds, finished);
        List<BatchResult> msgSuccess = messageMapper.insert(messages);
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

    @Transactional
    @Override
    public Result create(WorkOrderCreateParam param) {
        WorkOrderCreateVO workOrderCreateVO = new WorkOrderCreateVO();
        //参数校验
        if (param == null || StringUtils.isBlank(param.getTitle())
                || param.getType() == null || param.getPriorityLevel() == null
                || param.getDistributeId() == null || param.getCheckId() == null) {
            return Result.error("参数不完整");
        }
        WorkOrder workOrder = workOrderHelper.createWorkOrder(param);
        //插入新建工单
        boolean isSaved = this.save(workOrder);
        workOrderCreateVO.setId(workOrder.getId());
        workOrderCreateVO.setCode(workOrder.getCode());
        //更新handle_user_info表
        Staff staff = StaffHolder.get();
        workOrderHelper.addHandleInfo(workOrder.getId(), HandleTypeEnum.CREATED,
                0L, workOrder.getContent());
        workOrderHelper.addDistributeAndCheckInfo(workOrder.getId(), param.getDistributeId(), param.getCheckId());

        //发送信息
        Message message = workOrderHelper.buildMessage(WorkOrderStatusEnum.getByValue(workOrder.getStatus()), workOrder.getCode(), staff.getId());
        int msgSuccess = messageMapper.insert(message);
        long auditId = workOrderHelper.findAuditId(staff.getId());
        boolean isSuccess = dispatchToAuditor(workOrder.getId(), workOrder.getCode(), auditId, true);
        workOrderCreateVO.setIsSuccess(isSuccess && (msgSuccess == 1) && isSaved);
        return Result.success(workOrderCreateVO);
    }

    @Transactional
    @Override
    public boolean dispatchToAuditor(Long workOrderId, String workOrderCode, Long auditId, boolean isFirstAudit) {
        HandleTypeEnum handleType = HandleTypeEnum.AUDIT;
        //校验工单id和code不能全为空
        workOrderHelper.checkIdAndCodeNotNull(workOrderId, workOrderCode);
        //查询工单，如果不存在返回错误信息
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(workOrderId, workOrderCode);
        WorkOrder workOrder = getOne(workOrderWrapper);
        workOrderHelper.checkWorkOrderExist(workOrder);
        //校验工单状态和操作是否匹配
        workOrderHelper.checkHandleWorkOrderStatus(workOrder, handleType);
        //更新状态为待审核
        if (!isFirstAudit) {
            workOrderHelper.updateNextStatus(handleType, workOrder, false);
            updateById(workOrder);
        }
        Message message = workOrderHelper.buildMessage(AUDITING, workOrder.getCode(), auditId);
        int msgSuccess = messageMapper.insert(message);
        if (msgSuccess != 1) {
            return false;
        }
        //添加操作信息
        workOrderHelper.addHandleInfo(workOrder.getId(), handleType, auditId, "");
        return true;
    }

    @Transactional
    @Override
    public Result approval(WorkOrderApprovalParam param) {
        //查询工单，如果不存在返回错误信息
        LambdaQueryWrapper<WorkOrder> workOrderWrapper = WorkOrderQuery.getWorkOrderWrapper(param.getId(), param.getCode());
        WorkOrder workOrder = getOne(workOrderWrapper);
        workOrderHelper.checkWorkOrderExist(workOrder);
        //找到对应的handle—user-info数据，添加审批意见,更新finished位
        workOrderHelper.updateHandleUserInfo(param);
        WorkOrderApprovalVO workOrderApprovalVO = new WorkOrderApprovalVO();
        workOrderApprovalVO.setId(param.getId());
        workOrderApprovalVO.setCode(param.getCode());
        if (!param.getIsApproved()) {
            //审核不通过，流程结束
            workOrder.setStatus(WorkOrderStatusEnum.AUDIT_FAILURE.getValue());
            updateById(workOrder);
            workOrderApprovalVO.setResult(Boolean.TRUE);

            return Result.success(workOrderApprovalVO);
        }
        Long nextAuditId = workOrderHelper.findNextStaff();
        if (nextAuditId == null) {
            //没有下一个，已经结束了,更新状态
            workOrder.setStatus(WorkOrderStatusEnum.UNDISTRIBUTED.getValue());
            updateById(workOrder);
            workOrderApprovalVO.setResult(Boolean.TRUE);
            return Result.success(workOrderApprovalVO);
        }
        workOrderApprovalVO.setResult(Boolean.FALSE);
        //转发
        dispatchToAuditor(workOrder.getId(), workOrder.getCode(), nextAuditId, false);
        return Result.success(workOrderApprovalVO);
    }

    //5min检查一次数据库中延迟的工单
    @Async
    @Scheduled(fixedRate = 5 * 60 * 3600)
    @Transactional
    public void checkAndUpdateOverdueOrders() {
        // 查询所有状态是handle且deadlineTime已过的工单
        LocalDateTime now = LocalDateTime.now();
        Long nowTime = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        LambdaQueryWrapper<WorkOrder> getDelayWrapper = HandleUserInfoQuery.getByStatusAndDeadlineTime(nowTime, WorkOrderStatusEnum.HANDLING.getValue());
        List<WorkOrder> overdueOrders = this.list(getDelayWrapper);
        for (WorkOrder order : overdueOrders) {
            //更新工单状态
            LambdaUpdateWrapper<WorkOrder> updateWrapper = WorkOrderQuery.getUpdateStatusByIdWrapper(order.getId(), WorkOrderStatusEnum.DELAYED.getValue());
            update(updateWrapper);
            // 查工单的处理人
            List<Long> handleUserIds = workOrderHelper.getHandleUserIds(order.getId());
            // 发送通知给用户
            List<Message> messages = workOrderHelper.buildMessages(WorkOrderStatusEnum.DELAYED, order.getCode(), handleUserIds, true);
            List<BatchResult> success = messageMapper.insert(messages);
        }
        log.info("已完成一次延期工单扫描");
    }

    @Override
    public void export(WorkOrderPageParam param, HttpServletResponse response, HttpServletRequest request) {
        ExcelWriter excelWriter = null;
        String fileName_zh = formatterDate.format(LocalDateTime.now()) + "工单";

        List<WorkOrderPageVO> pageVOS = this.pageWorkOrder(param).getRecords();
        List<WorkOrderExportVO> excelVOS = WorkOrderConverter.INSTANCE.toExcelVOS(pageVOS);
        Map<String, List<HandleUserInfoVO>> collectHandle = pageVOS.stream().filter(vo -> vo.getHandlerInfo() != null).collect(Collectors.toMap(WorkOrderPageVO::getCode, WorkOrderPageVO::getHandlerInfo));
        Map<String, List<HandleUserInfoVO>> collectAudit = pageVOS.stream().filter(vo -> vo.getHandlerInfo() != null).collect(Collectors.toMap(WorkOrderPageVO::getCode, WorkOrderPageVO::getAuditorInfo));
        for (WorkOrderExportVO exportVO : excelVOS) {
            String code = exportVO.getCode();
            List<HandleUserInfoVO> handleInfos = collectHandle.getOrDefault(code, null);
            List<HandleUserInfoVO> auditInfos = collectAudit.getOrDefault(code, null);
            if (CollectionUtils.isNotEmpty(handleInfos)) {
                List<String> handleTimes = handleInfos.stream().filter(HandleUserInfoVO::getFinished).map(HandleUserInfoVO::getHandleTime).toList();
                List<Long> handleTimeStamps = handleTimes.stream().map(handleTime -> LocalDateTime.parse(handleTime, formatter).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).toList();
                Long maxHandleTimeStamps = handleTimeStamps.stream().max(Comparator.comparingLong(Long::longValue)).orElse(null);
                exportVO.setFinishTime(maxHandleTimeStamps != null ? formatter.format(Instant.ofEpochSecond(maxHandleTimeStamps).atZone(ZoneId.systemDefault()).toLocalDateTime()) : null);
            }
            if (CollectionUtils.isNotEmpty(auditInfos)) {
                List<String> auditTimes = auditInfos.stream().filter(HandleUserInfoVO::getFinished).map(HandleUserInfoVO::getHandleTime).toList();
                List<Long> auditTimeStamps = auditTimes.stream().map(handleTime -> LocalDateTime.parse(handleTime, formatter).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()).toList();
                Long maxAuditTimeStamps = auditTimeStamps.stream().max(Comparator.comparingLong(Long::longValue)).orElse(null);
                exportVO.setFinishedAuditTime(maxAuditTimeStamps != null ? formatter.format(Instant.ofEpochSecond(maxAuditTimeStamps).atZone(ZoneId.systemDefault()).toLocalDateTime()) : null);
            }
        }

        try {
            //设置文件类型和编码格式
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 设置表头样式
            WriteCellStyle headStyle = new WriteCellStyle();
            headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            // 设置表格内容样式
            WriteCellStyle bodyStyle = new WriteCellStyle();
            bodyStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置文件名
            String fileName = URLEncoder.encode(fileName_zh, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName+".xlsx");
            // 读文件
            excelWriter = EasyExcel.write(response.getOutputStream())
                    .needHead(true)
                    .excelType(ExcelTypeEnum.XLSX)
                    .build();
            WriteSheet sheet = EasyExcel.writerSheet("工单信息").head(WorkOrderExportVO.class).sheetNo(1).build();
            excelWriter.write(excelVOS, sheet);
        } catch (Exception e) {
            log.error("export excel {} error", fileName_zh, e);
        } finally {
            assert excelWriter != null;
            excelWriter.finish();
        }
    }
}
