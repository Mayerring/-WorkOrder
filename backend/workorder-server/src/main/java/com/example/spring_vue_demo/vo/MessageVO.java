package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/07
 */
@Data
public class MessageVO {

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型")
    private Integer type;

    @Schema(description = "消息类型desc")
    private String typeDesc;

    @Schema(description = "发送时间")
    private String sendTime;
}
