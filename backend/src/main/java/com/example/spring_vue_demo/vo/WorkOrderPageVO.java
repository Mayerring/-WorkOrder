package com.example.spring_vue_demo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.spring_vue_demo.common.TimeTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.LongTypeHandler;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author wtt
 * @date 2025/05/24
 */
@Data
public class WorkOrderPageVO {
    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "工单类型")
    private Integer type;

    @Schema(description = "提出部门id")
    private Long departmentId;

    @Schema(description = "提出部门名称")
    private String departmentName;

    @Schema(description = "审批人")
    private String approver;

    @Schema(description = "优先级")
    private Integer priorityLevel;

    @Schema(description = "处理人")
    private String handler;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "取消时间")
    private String cancelTime;

    @Schema(description = "删除时间")
    private String deleteTime;

    @Schema(description = "截止时间")
    private String deadlineTime;

    @Schema(description = "完成时间")
    private String finishTime;

    @Schema(description = "附件url")
    private String accessoryUrl;

    @Schema(description = "附件文件名")
    private String accessoryName;
}
