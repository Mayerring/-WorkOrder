package com.example.spring_vue_demo.enums;

import lombok.Getter;

import java.util.Objects;
@Getter
public enum WorkOrderPriorityLevelEnum {
    HIGH(0,"高"),
    MID(1,"中"),
    LOW(2,"低")
    ;
    private final Integer value;
    private final String desc;
    WorkOrderPriorityLevelEnum(Integer value,String desc){
        this.value=value;
        this.desc=desc;
    }
    public static WorkOrderPriorityLevelEnum getByValue(Integer value){
        for(WorkOrderPriorityLevelEnum workOrderPriorityLevelEnum : WorkOrderPriorityLevelEnum.values()){
            if(Objects.equals(workOrderPriorityLevelEnum.value, value)){
                return workOrderPriorityLevelEnum;
            }
        }
        return null;
    }
}
