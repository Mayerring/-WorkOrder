package com.example.spring_vue_demo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WorkOrderCreateVO {
    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "是否成功创建")
    private Boolean isSuccess;
}
