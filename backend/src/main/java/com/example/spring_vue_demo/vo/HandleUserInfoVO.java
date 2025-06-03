package com.example.spring_vue_demo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/06/01
 */
@Data
public class HandleUserInfoVO {
    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "工单id")
    private Long orderId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "处理类型（1：提交人，2：审核人，3：派单人，4：处理人，5：确认人")
    private Integer handleType;

    @Schema(description = "处理类型desc")
    private String handleTypeDesc;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "公司id")
    private Long companyId;

    @Schema(description = "公司名")
    private String companyName;

    @Schema(description = "部门id")
    private Long departmentId;

    @Schema(description = "部门名")
    private String departmentName;

    @Schema(description = "操作时间")
    private String handleTime;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "操作备注")
    private String remark;
}
