package com.example.workorder.api.enums;

import lombok.Getter;

@Getter
public enum WorkOrderTypeEnum {

    REQUIREMENT(0, "需求"),
    FAULT(1, "故障");

    private final Integer type;
    private final String desc;

    WorkOrderTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getDesc(Integer type) {
        for (WorkOrderTypeEnum e : values()) {
            if (e.type.equals(type)) {
                return e.desc;
            }
        }
        return "";
    }
}