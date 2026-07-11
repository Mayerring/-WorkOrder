package com.example.workorder.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.workorder.api.entity.SearchResult;
import com.example.workorder.api.entity.WorkOrder;
import com.example.workorder.api.param.WorkOrder.WorkOrderDetailParam;
import com.example.workorder.api.param.WorkOrder.WorkOrderPageParam;
import com.example.workorder.api.vo.WorkOrder.WorkOrderDetailVO;
import com.example.workorder.api.vo.WorkOrder.WorkOrderPageVO;

import java.io.IOException;

public interface WorkOrderQueryApi {

    IPage<WorkOrderPageVO> pageWorkOrder(WorkOrderPageParam param);

    WorkOrderDetailVO detail(WorkOrderDetailParam param);

    SearchResult<WorkOrder> searchWorkOrders(String keyword, int pageNum, int pageSize) throws IOException;
}