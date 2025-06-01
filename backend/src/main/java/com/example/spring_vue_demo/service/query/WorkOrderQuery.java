package com.example.spring_vue_demo.service.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.param.WorkOrderDetailParam;
import com.example.spring_vue_demo.param.WorkOrderPageParam;
import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author wtt
 * @date 2025/05/31
 */
public class WorkOrderQuery {

    public static LambdaQueryWrapper<WorkOrder> getPageWorkWrapper(WorkOrderPageParam param, List<Long> orderIds) {
        LambdaQueryWrapper<WorkOrder> pageWrapper=new LambdaQueryWrapper<WorkOrder>()
                .eq(Objects.nonNull(param.getId()),WorkOrder::getId,param.getId())
                .in(CollectionUtils.isNotEmpty(orderIds),WorkOrder::getId,orderIds)
                .eq(StringUtils.isNotBlank(param.getCode()),WorkOrder::getCode,param.getCode())
                .eq(Objects.nonNull(param.getType()),WorkOrder::getType,param.getType())
                .eq(Objects.nonNull(param.getPriorityLevel()),WorkOrder::getPriorityLevel,param.getPriorityLevel())
                .in(CollectionUtils.isNotEmpty(param.getStatus()),WorkOrder::getStatus,param.getStatus())
                .ge(StringUtils.isNotBlank(param.getCreateTimeFrom()),WorkOrder::getCreateTime,param.getCreateTimeFrom())
                .le(StringUtils.isNotBlank(param.getCreateTimeTo()),WorkOrder::getCancelTime,param.getCreateTimeTo())
                ;
        return pageWrapper;
    }

    public static Page<WorkOrder> getPageInfoWrapper(WorkOrderPageParam param){
        Page<WorkOrder> pageWrapper=new Page<WorkOrder>();
        pageWrapper.setCurrent(param.getPageNum());
        pageWrapper.setSize(param.getPageSize());
        return pageWrapper;
    }

    public static LambdaQueryWrapper<WorkOrder> getDetailWorkWrapper(WorkOrderDetailParam param) {
        LambdaQueryWrapper<WorkOrder>wrapper=new LambdaQueryWrapper<WorkOrder>()
                .eq(Objects.nonNull(param.getId()),WorkOrder::getId,param.getId())
                .eq(StringUtils.isNotBlank(param.getCode()),WorkOrder::getCode,param.getCode());
        return wrapper;
    }
}
