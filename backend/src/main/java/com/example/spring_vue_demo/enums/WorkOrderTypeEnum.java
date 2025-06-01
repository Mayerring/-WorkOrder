package com.example.spring_vue_demo.enums;

import java.util.Objects;

public enum WorkOrderTypeEnum {
    REQUIREMENTS(0,"需求"),
    FAULT(1,"故障")
    ;
    private final Integer value;
    private final String desc;
    WorkOrderTypeEnum(Integer value,String desc){
        this.value=value;
        this.desc=desc;
    }
    public static WorkOrderTypeEnum getByValue(Integer value){
        for(WorkOrderTypeEnum workOrderTypeEnum : WorkOrderTypeEnum.values()){
            if(Objects.equals(workOrderTypeEnum.value, value)){
                return workOrderTypeEnum;
            }
        }
        return null;
    }
    public Integer getValue(){
        return value;
    }
    public String getDesc(){return desc;}

}
