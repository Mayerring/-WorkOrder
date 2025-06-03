package com.example.spring_vue_demo.controller.admin;

import com.example.spring_vue_demo.entity.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author WangDayu
 * @date 2025/6/1
 */

@RestController
@RequiredArgsConstructor
@Tag(name="职工管理")
@RequestMapping("/staff")
public class StaffController {


    @ApiOperationSupport(order = 1)
    @Operation(summary = "新增员工")
    @PostMapping("/add")
    public Result addStaff()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 2)
    @Operation(summary = "删除员工")
    @PostMapping("/delete")
    public Result deleteStaff()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 3)
    @Operation(summary = "修改员工信息")
    @PostMapping("/change")
    public Result changeStaff()
    {
        return Result.error("暂未实现");
    }

    @ApiOperationSupport(order = 4)
    @Operation(summary = "分页查看员工信息")
    @PostMapping("/page")
    public Result staffChange()
    {
        return Result.error("暂未实现");
    }
}
