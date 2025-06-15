package com.example.spring_vue_demo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spring_vue_demo.param.MessageParam;
import com.example.spring_vue_demo.param.StatusDataParam;
import com.example.spring_vue_demo.param.WeekHandleQuantityParam;
import com.example.spring_vue_demo.vo.*;
import com.example.spring_vue_demo.vo.WorkOrder.WorkOrderDataVO;
import com.example.spring_vue_demo.vo.WorkOrder.WorkOrderTodoVO;

import java.util.List;

public interface DashboardService {

    WorkOrderDataVO getData();

    List<WorkOrderTodoVO> getTodo();

    List<StatusDataVO>  getStatus(StatusDataParam param);

    WeekHandleVO getWeekHandleQuantity(WeekHandleQuantityParam param);

    IPage<MessageVO> pageMessages(MessageParam param);
}
