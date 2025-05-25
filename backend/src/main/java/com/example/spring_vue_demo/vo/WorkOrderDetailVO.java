package com.example.spring_vue_demo.vo;

import com.example.spring_vue_demo.entity.HandleUserInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/24
 */
public class WorkOrderDetailVO {
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "工单类型，0需求，1故障")
    private Integer type;

    @Schema(description = "工单标题")
    private String title;

    @Schema(description = "提交信息")
    private HandleUserInfo submitterInfo;

    @Schema(description = "审批信息")
    private List<HandleUserInfo> auditorInfo;

    @Schema(description = "派单信息")
    private HandleUserInfo distributerInfo ;

    @Schema(description = "处理信息")
    private List<HandleUserInfo> handlerInfo;

    @Schema(description = "确认信息")
    private HandleUserInfo checkerInfo ;

    @Schema(description = "优先级，0高，1中，2低")
    private Integer priorityLevel;

    @Schema(description = "状态，100未审核，200审核中，300未派单，400处理中，410已超时，500已完成，600已确认完成，700已取消，800异常（审核失败）")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "取消时间")
    private String cancelTime;

    @Schema(description = "删除时间")
    private String deleteTime;

    @Schema(description = "详情")
    private String content;

    @Schema(description = "附件url")
    private String accessoryUrl;

    @Schema(description = "附件文件名")
    private String accessoryName;

}
