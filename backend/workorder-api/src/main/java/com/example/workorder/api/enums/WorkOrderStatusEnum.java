package com.example.workorder.api.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatusEnum {

    UN_AUDITED(100, "未审核"),
    AUDITING(200, "审核中"),
    AUDIT_FAILED(270, "审核失败"),
    UN_ASSIGNED(300, "未派单"),
    HANDLING(400, "处理中"),
    TIMEOUT(410, "已超时"),
    COMPLETED(500, "已完成"),
    CONFIRMED_COMPLETE(600, "已确认完成"),
    CONFIRM_FAILED(670, "确认失败"),
    CANCELLED(700, "已取消");

    private final Integer status;
    private final String desc;

    WorkOrderStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDesc(Integer status) {
        for (WorkOrderStatusEnum e : values()) {
            if (e.status.equals(status)) {
                return e.desc;
            }
        }
        return "";
    }
}