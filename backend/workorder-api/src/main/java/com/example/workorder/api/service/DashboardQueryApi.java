package com.example.workorder.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.workorder.api.param.MessageParam;
import com.example.workorder.api.vo.MessageVO;
import com.example.workorder.api.vo.WeekHandleVO;
import com.example.workorder.api.vo.WorkOrder.WorkOrderDataVO;

import java.util.List;

public interface DashboardQueryApi {

    WorkOrderDataVO getData();

    List<WeekHandleVO> getWeekHandleQuantity();

    IPage<MessageVO> pageMessages(MessageParam param);
}