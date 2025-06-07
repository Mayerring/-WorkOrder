package com.example.spring_vue_demo.service;


import com.example.spring_vue_demo.param.StatusDataParam;
import com.example.spring_vue_demo.param.WeekHandleQuantityParam;
import com.example.spring_vue_demo.vo.StatusDataVO;
import com.example.spring_vue_demo.vo.WeekHandleVO;
import com.example.spring_vue_demo.vo.WorkOrderDataVO;
import com.example.spring_vue_demo.vo.WorkOrderTodoVO;

import java.util.List;

public interface DashboardService {

    WorkOrderDataVO getData();

    List<WorkOrderTodoVO> getTodo();

    List<StatusDataVO>  getStatus(StatusDataParam param);

    WeekHandleVO getWeekHandleQuantity(WeekHandleQuantityParam param);

    Object getMessages(Object param);
}
