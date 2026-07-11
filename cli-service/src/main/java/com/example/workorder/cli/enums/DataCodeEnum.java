package com.example.workorder.cli.enums;

import lombok.Getter;

@Getter
public enum DataCodeEnum {

    WORK_ORDER_PAGE("work_order_page", "工单分页查询", "分页查询工单列表", "POST", "/workOrder/page", "all"),
    WORK_ORDER_DETAIL("work_order_detail", "工单详情", "查询工单详细信息", "POST", "/workOrder/detail", "all"),
    WORK_ORDER_SEARCH("work_order_search", "工单搜索", "根据关键词搜索工单", "GET", "/workOrder/search", "all"),

    DASHBOARD_DATA("dashboard_data", "数据看板", "获取数据统计概览", "POST", "/dashboard/data", "all"),
    DASHBOARD_HANDLE_QUANTITY("dashboard_handle_quantity", "本周处理数量", "获取本周处理统计", "POST", "/dashboard/handleQuantity", "all"),
    DASHBOARD_MESSAGES("dashboard_messages", "消息中心", "获取消息列表", "POST", "/dashboard/pageMessages", "all"),
    
    FLOW_GET_BY_ID("flow_get_by_id", "流程详情", "查询流程详细信息", "POST", "/flow/getById", "all"),
    FLOW_PAGE("flow_page", "流程分页", "分页查询流程列表", "POST", "/flow/page", "all");
   

    private final String dataCode;
    private final String name;
    private final String description;
    private final String httpMethod;
    private final String endpoint;
    private final String permission;

    DataCodeEnum(String dataCode, String name, String description, String httpMethod, String endpoint, String permission) {
        this.dataCode = dataCode;
        this.name = name;
        this.description = description;
        this.httpMethod = httpMethod;
        this.endpoint = endpoint;
        this.permission = permission;
    }

    public static DataCodeEnum fromDataCode(String dataCode) {
        for (DataCodeEnum e : values()) {
            if (e.dataCode.equals(dataCode)) {
                return e;
            }
        }
        return null;
    }
}