package com.example.spring_vue_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Data
@TableName("handle_user_info")
@AllArgsConstructor
public class HandleUserInfo {

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "工单id")
    private Long orderId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "处理类型（1：提交人，2：审核人，3：派单人，4：处理人，5：确认人")
    private Integer handleType;

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

    @TableLogic
    @Schema(description = "删除位")
    private Integer deleted;
}
