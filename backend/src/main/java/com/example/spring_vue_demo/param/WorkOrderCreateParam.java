/**
 *
 * @author WangDayu
 * @date 2025/5/31
 */


package com.example.spring_vue_demo.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.spring_vue_demo.common.TimeTypeHandler;
import com.example.spring_vue_demo.entity.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class WorkOrderCreateParam {
    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单类型")
    private Integer type;
    //todo:把信息改成String，在param中存的HandlerInfo转成json落库

    @Schema(description = "工单标题")
    private String title;
    @Schema(description = "详情")
    private String content;
    @Schema(description = "提交信息")
    private UserInfo submitterInfo;

    @Schema(description = "优先级，0高，1中，2低")
    private Integer priorityLevel;

    @Schema(description = "附件url")
    private String accessoryUrl;

    @Schema(description = "附件文件名")
    private String accessoryName;
}
