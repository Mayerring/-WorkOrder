package com.example.spring_vue_demo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author wtt
 * @date 2025/06/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderExportVO {

    @ColumnWidth(25)
    @ExcelProperty(value = "工单编号")
    private String code;

    @ExcelProperty(value = "工单类型")
    private String typeDesc;

    @ExcelProperty(value = "工单标题")
    private String title;

    @ExcelProperty(value = "优先级")
    private String priorityLevelDesc;

    @ColumnWidth(15)
    @ExcelProperty(value = "状态")
    private String statusDesc;

    @ExcelProperty(value = "提交人")
    private String submitter;

    @ColumnWidth(20)
    @ExcelProperty(value = "提交时间")
    private String createTime;

    @ExcelProperty(value = "审批人")
    private String auditors;

    @ColumnWidth(20)
    @ExcelProperty(value="完成审批时间")
    private String finishedAuditTime;

    @ExcelProperty(value = "派单人")
    private String distributer ;

    @ColumnWidth(20)
    @ExcelProperty(value = "派单时间")
    private String distributeTime ;

    @ExcelProperty(value = "处理人")
    private String handlers;

    @ColumnWidth(20)
    @ExcelProperty(value = "完成工单时间")
    private String finishTime;

    @ExcelProperty(value = "验收人")
    private String checker ;

    @ColumnWidth(20)
    @ExcelProperty(value = "验收时间")
    private String checkTime ;

    @ColumnWidth(20)
    @ExcelProperty(value = "更新时间")
    private String updateTime;

    @ColumnWidth(20)
    @ExcelProperty(value = "取消时间")
    private String cancelTime;

    @ColumnWidth(20)
    @ExcelProperty(value = "删除时间")
    private String deleteTime;

    @ColumnWidth(20)
    @ExcelProperty(value = "截止时间")
    private String deadlineTime;
}
