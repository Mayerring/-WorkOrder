package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Flow;
import com.example.spring_vue_demo.param.Flow.FlowCreateParam;
import com.example.spring_vue_demo.param.Flow.FlowIdParam;
import com.example.spring_vue_demo.param.Flow.FlowPageParam;
import com.example.spring_vue_demo.param.Flow.FlowUpdateParam;
import com.example.spring_vue_demo.vo.Flow.FlowCreateVO;
import com.example.spring_vue_demo.vo.Flow.FlowNodeVO;
import com.example.spring_vue_demo.vo.Flow.FlowVO;
import org.springframework.stereotype.Service;

@Service
public interface FlowService extends IService<Flow> {
    FlowCreateVO create(FlowCreateParam param,Long flowId);

    FlowCreateVO  update(FlowUpdateParam param);

    boolean delete(FlowIdParam param);


    FlowVO getByFlowId(FlowIdParam param);

    Page<FlowVO> page(FlowPageParam param);
    public FlowNodeVO getNextFlowNode(Long curHandlerId,Long flowId);
}
