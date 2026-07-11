package com.example.workorder.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.workorder.api.param.Flow.FlowIdParam;
import com.example.workorder.api.param.Flow.FlowPageParam;
import com.example.workorder.api.vo.Flow.FlowVO;

public interface FlowQueryApi {

    FlowVO getByFlowId(FlowIdParam param);

    IPage<FlowVO> page(FlowPageParam param);
}