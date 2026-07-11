package com.example.workorder.api.enums;

import lombok.Getter;

@Getter
public enum WorkOrderPriorityLevelEnum {

    HIGH(0, "高"),
    MEDIUM(1, "中"),
    LOW(2, "低");

    private final Integer level;
    private final String desc;

    WorkOrderPriorityLevelEnum(Integer level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public static String getDesc(Integer level) {
        for (WorkOrderPriorityLevelEnum e : values()) {
            if (e.level.equals(level)) {
                return e.desc;
            }
        }
        return "";
    }
}