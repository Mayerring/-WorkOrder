package com.example.spring_vue_demo.service.convert;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.Flow;
import com.example.spring_vue_demo.param.Flow.FlowNode;
import com.example.spring_vue_demo.vo.Flow.FlowVO;
import com.example.spring_vue_demo.vo.Flow.FlowNodeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @date 2025/06/14
 */
@Mapper
public interface FlowConverter {
    FlowConverter INSTANCE= Mappers.getMapper(FlowConverter.class);

    @Mapping(target = "flowId", source = "flowId") // 从参数传入的flowId
    Flow toFlow(FlowNode param, Long flowId);

    default List<Flow> toFlowList(List<FlowNode> nodes, Long flowId) {
        return nodes.stream().map(node -> toFlow(node, flowId)).collect(Collectors.toList());
    }

    default FlowVO toFlowVO(List<Flow> flowList){
        if(CollectionUtils.isEmpty(flowList)){
            return null;
        }
        FlowVO flowVO=new FlowVO();
        flowVO.setFlowId(flowList.get(0).getFlowId());
        flowVO.setNodes(flowList.stream().map(this::toNodeVO).collect(Collectors.toList()));
        return flowVO;
    }

    @Mapping(target = "nodeTypeDesc",expression = "java(com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum.getByValue(flow.getNodeType()).getDesc() )")
    FlowNodeVO toNodeVO(Flow flow);

    default Page<FlowVO> toPageVO(Page<Flow> flowPage){
        List<Flow> flowList = flowPage.getRecords();
        Map<Long, List<Flow>> collect = flowList.stream().collect(Collectors.groupingBy(Flow::getFlowId));
        List<FlowVO>flowVOS=new ArrayList<>();
        for( Map.Entry<Long, List<Flow>>entry:collect.entrySet()){
            FlowVO flowVO=toFlowVO(entry.getValue());
            flowVOS.add(flowVO);
        }
        Page<FlowVO>flowVOPage=new Page<>();
        flowVOPage.setCurrent(flowPage.getCurrent());
        flowVOPage.setSize(flowPage.getSize());
        flowVOPage.setRecords(flowVOS);
        flowVOPage.setTotal(flowPage.getTotal());
        return flowVOPage;
    }


}
