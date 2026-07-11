package com.example.spring_vue_demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.spring_vue_demo.common.TimeTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @date 2025/06/01
 */
@Data
@TableName(value = "message",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "接收人id")
    private Long receiverId;

    @Schema(description = "发送人id")
    private Long senderId;

    @Schema(description = "消息类型（对应工单状态）")
    private Integer type;

    @TableField(exist = false)
    @Schema(description = "类型desc")
    private String typeDesc;

    @Schema(description = "内容")
    private String content;

    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "发送时间")
    private String sendTime;

    @TableLogic
    @Schema(description = "删除位")
    private Integer deleted;

}
