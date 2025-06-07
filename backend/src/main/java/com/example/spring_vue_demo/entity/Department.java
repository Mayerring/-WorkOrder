package com.example.spring_vue_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.spring_vue_demo.common.TimeTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("department")
public class Department {
    @TableId(type = IdType.AUTO)
    private int id;
    private String name;
    private String code;
    private String parentDepartmentCode;
    private String companyCode;
    private String leaderNumber;

    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "创建时间")
    private String createTime;
    @TableField(typeHandler = TimeTypeHandler.class)
    @Schema(description = "更新时间")
    private String updateTime;

}
