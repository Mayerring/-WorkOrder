package com.example.spring_vue_demo.enums;

import java.util.Objects;

public enum WorkOrderPriorityLevelEnum {
    HIGH(0,"高"),
    MID(1,"中"),
    LOW(2,"低")
    ;
    private Integer value;
    private String desc;
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
    public Integer getValue(){
        return value;
    }
    public String getDesc(){return desc;}
}
