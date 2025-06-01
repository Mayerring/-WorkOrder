package com.example.spring_vue_demo.enums;

import java.util.Objects;

/**
 * @author wtt
 * @date 2025/06/01
 */
public enum WorkOrderStatusEnum {
    UNAUDITED(100,"未审核"),
    AUDITING(200,"审核中"),
    UNDISTRIBUTED(300,"未派单"),
    HANDLING(400,"处理中"),
    DELAYED(410,"已超时"),
    FINISHED(500,"已完成"),
    CHECKED(600,"已确认完成"),
    CANCELLED(700,"已取消"),
    AUDIT_FAILURE(810,"审核失败"),
    CHECK_FAILURE(820,"确认失败")
    ;
    private Integer value;
    private String desc;
    WorkOrderStatusEnum(Integer value,String desc){
        this.value=value;
        this.desc=desc;
    }
    public static WorkOrderStatusEnum getByValue(Integer value){
        for(WorkOrderStatusEnum workOrderStatusEnum : WorkOrderStatusEnum.values()){
            if(Objects.equals(workOrderStatusEnum.value, value)){
                return workOrderStatusEnum;
            }
        }
        return null;
    }
    public Integer getValue(){
        return value;
    }
    public String getDesc(){return desc;}
}
