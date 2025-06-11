package com.example.spring_vue_demo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderApprovalVO {
    @TableId(type = IdType.AUTO)
    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "工单编号")
    private String code;

    @Schema(description = "审核结果，0表示结束，1表示未结束")
    private Boolean result;

}
