package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WorkOrderPageParam extends BasePageParam{

    //查询条件
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "工单类型")
    private Integer type;

    @Schema(description = "提交信息")
    private List<HandleUserInfoParam> submitterInfo;

    @Schema(description = "审批信息")
    private List<HandleUserInfoParam> auditorInfo;

    @Schema(description = "派单信息")
    private List<HandleUserInfoParam> distributerInfo ;

    @Schema(description = "处理信息")
    private List<HandleUserInfoParam> handleInfo;

    @Schema(description = "确认信息")
    private List<HandleUserInfoParam> checkerInfo ;

    @Schema(description = "优先级，0高，1中，2低")
    private Integer priorityLevel;

    @Schema(description = "状态，100未审核，200审核中，270审核失败，300未派单，400处理中，410已超时，500已完成，600已确认完成，670确认失败，700已取消")
    private List<Integer> status;

    @Schema(description = "创建时间起")
    private Long createTimeFrom;

    @Schema(description = "创建时间止")
    private Long createTimeTo;
}
