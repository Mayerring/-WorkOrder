package com.example.spring_vue_demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.param.Flow.FlowCreateParam;
import com.example.spring_vue_demo.param.Flow.FlowIdParam;
import com.example.spring_vue_demo.param.Flow.FlowPageParam;
import com.example.spring_vue_demo.param.Flow.FlowUpdateParam;
import com.example.spring_vue_demo.service.FlowService;
import com.example.spring_vue_demo.vo.Flow.FlowCreateVO;
import com.example.spring_vue_demo.vo.Flow.FlowVO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * @author wtt
 * @date 2025/06/14
 */
@RestController
@RequestMapping("/flow")
@AllArgsConstructor
@Tag(name = "FlowController", description = "工单流程管理接口")
public class FlowController {
    private final FlowService flowService;

    @ApiOperationSupport(order = 1)
    @PostMapping("/create")
    @Operation(summary = "新增流程")
    public FlowCreateVO create(@RequestBody FlowCreateParam param) {
        return flowService.create(param,null);
    }

    @ApiOperationSupport(order = 2)
    @PostMapping("/edit")
    @Operation(summary = "编辑流程")
    public FlowCreateVO edit(@RequestBody FlowUpdateParam param) {
        return flowService.update(param);
    }

    @ApiOperationSupport(order = 3)
    @PostMapping("/delete")
    @Operation(summary = "删除流程")
    public boolean delete(@RequestBody FlowIdParam param){
        return flowService.delete(param);
    }

    @ApiOperationSupport(order = 4)
    @PostMapping("/getById")
    @Operation(summary = "根据流程ID查询")
    public FlowVO getByFlowId(@RequestBody FlowIdParam param) {
        return flowService.getByFlowId(param);
    }

    @ApiOperationSupport(order = 5)
    @PostMapping("/page")
    @Operation(summary = "分页查询")
    public Page<FlowVO> pageByFlowId(@RequestBody FlowPageParam param){
        return flowService.page(param);
    }
}
