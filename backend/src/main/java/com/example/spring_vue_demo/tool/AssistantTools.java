 package com.example.spring_vue_demo.tool;

 import com.example.spring_vue_demo.entity.SearchResult;
 import com.example.spring_vue_demo.entity.WorkOrder;
 import com.example.spring_vue_demo.service.WorkOrderService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 import java.io.IOException;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @author wtt
 * @date 2026/02/28
 */
 @Component
 public class AssistantTools {
     @Autowired
     private WorkOrderService workOrderService;
     
     @Tool(description = "根据关键词搜索工单信息，keyword传关键词，pageNum默认传1，pageSize默认传10，返回查询到的工单列表、数量")
     public String searchWorkOrders(String keyword, int pageNum, int pageSize) throws IOException {

         SearchResult<WorkOrder>result= workOrderService.searchWorkOrders(keyword,pageNum,pageSize);
         // 格式化工单信息为可读文本
         String formattedResult = formatWorkOrders(result);

         // 返回格式化的文本，MCP 会自动包装成正确的 JSON-RPC
         return formattedResult;
     }

     private String formatWorkOrders(SearchResult<WorkOrder> result) {
         StringBuilder sb = new StringBuilder();
         sb.append("根据您搜索的关键词，找到以下工单：\n\n");

         for (WorkOrder order : result.getList()) {
             sb.append("【工单").append(order.getId()).append("】\n");
             sb.append("- 工单编号：").append(order.getCode()).append("\n");
             sb.append("- 工单类型：").append(order.getType() == 0 ? "需求" : "故障").append("\n");
             sb.append("- 标题：").append(order.getTitle()).append("\n");

             // 转换优先级
             String priority = switch (order.getPriorityLevel()) {
                 case 0 -> "高";
                 case 1 -> "中";
                 case 2 -> "低";
                 default -> "未知";
             };
             sb.append("- 优先级：").append(priority).append("\n");

             // 转换状态
             String status = convertStatus(order.getStatus());
             sb.append("- 状态：").append(status).append("\n");
             sb.append("- 创建时间：").append(order.getCreateTime()).append("\n");

             // 展示处理人信息
             if (order.getSubmitterInfo() != null) {
                 sb.append("- 提交人：").append(order.getSubmitterInfo().getUserName())
                         .append(" (").append(order.getSubmitterInfo().getCompanyName())
                         .append("/").append(order.getSubmitterInfo().getDepartmentName()).append(")");
                 if (order.getSubmitterInfo().getHandleTime() != null) {
                     sb.append(" - ").append(order.getSubmitterInfo().getHandleTime());
                 }
                 sb.append("\n");
             }

             // 其他处理人信息...
             sb.append("\n");
         }

         sb.append("共找到").append(result.getTotal()).append("条工单记录，")
                 .append("当前显示第").append(result.getPageNum()).append("页，")
                 .append("每页显示").append(result.getPageSize()).append("条。");

         return sb.toString();
     }

     private String convertStatus(Integer status) {
         return switch (status) {
             case 100 -> "未审核";
             case 200 -> "审核中";
             case 270 -> "审核失败";
             case 300 -> "未派单";
             case 400 -> "处理中";
             case 410 -> "已超时";
             case 500 -> "已完成";
             case 600 -> "已确认完成";
             case 670 -> "确认失败";
             case 700 -> "已取消";
             default -> "未知状态";
         };
     }
 }
