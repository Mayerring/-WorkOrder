package com.example.spring_vue_demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.spring_vue_demo.common.TimeTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
@TableName(value="work_order",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrder {

    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "工单类型，0需求，1故障")
    private Integer type;

    @Schema(description = "工单标题")
    private String title;

    @Schema(description = "流程id")
    private Long flowId;

    @TableField(exist = false)
    @Schema(description = "提交信息")
    private HandleUserInfo submitterInfo;

    @TableField(exist = false)
    @Schema(description = "审批信息")
    private List<HandleUserInfo> auditorInfo;

    @TableField(exist = false)
    @Schema(description = "派单信息")
    private HandleUserInfo distributerInfo ;

    @TableField(exist = false)
    @Schema(description = "处理信息")
    private List<HandleUserInfo> handlerInfo;

    @TableField(exist = false)
    @Schema(description = "确认信息")
    private HandleUserInfo checkerInfo ;

    @Schema(description = "优先级，0高，1中，2低")
    private Integer priorityLevel;

    @Schema(description = "状态，100未审核，200审核中，270审核失败，300未派单，400处理中，410已超时，500已完成，600已确认完成，670确认失败，700已取消")
    private Integer status;

    @TableField(typeHandler = TimeTypeHandler.class,fill=FieldFill.INSERT)
    @Schema(description = "创建时间")
    private String createTime;

    @TableField(typeHandler = TimeTypeHandler.class,fill=FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private String updateTime;

    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "取消时间")
    private String cancelTime;

    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "删除时间")
    private String deleteTime;

    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "截止时间")
    private String deadlineTime;

    @Schema(description = "详情")
    private String content;

    @Schema(description = "附件url")
    private String accessoryUrl;

    @Schema(description = "附件文件名")
    private String accessoryName;

    @TableLogic
    @Schema(description = "删除位")
    private Integer deleted;
}
