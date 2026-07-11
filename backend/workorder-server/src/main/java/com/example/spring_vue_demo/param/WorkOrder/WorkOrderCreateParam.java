/**
 *
 * @author WangDayu
 * @date 2025/5/31
 */


package com.example.spring_vue_demo.param.WorkOrder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class WorkOrderCreateParam {
    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单类型")
    private Integer type;

    @Schema(description = "工单标题")
    private String title;

    @Schema(description = "详情")
    private String content;

    @Schema(description = "优先级，0高，1中，2低")
    private Integer priorityLevel;

    @Schema(description = "流程Id")
    private Long flowId;

    @Schema(description = "截止时间")
    private Long deadlineTime;

    @Schema(description = "附件url")
    private String accessoryUrl;

    @Schema(description = "附件文件名")
    private String accessoryName;
}
