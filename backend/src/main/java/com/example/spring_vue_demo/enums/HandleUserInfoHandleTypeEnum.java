package com.example.spring_vue_demo.enums;

import java.util.Objects;

/**
 * @author wtt
 * @date 2025/05/31
 */
public enum HandleUserInfoHandleTypeEnum {
    SUBMITTER(1,"提交人"),
    AUDITOR(2,"审核人"),
    DISTRIBUTOR(3,"派单人"),
    HANDLER(4,"处理人"),
    CHECKER(5,"确认人")
    ;
    private final Integer value;
    private final String desc;
    HandleUserInfoHandleTypeEnum(Integer value,String desc){
        this.value=value;
        this.desc=desc;
    }
    public static HandleUserInfoHandleTypeEnum getByValue(Integer value){
        for(HandleUserInfoHandleTypeEnum typeEnum: HandleUserInfoHandleTypeEnum.values()){
            if(Objects.equals(typeEnum.value, value)){
                return typeEnum;
            }
        }
        return null;
    }

    public Integer getValue(){
        return value;
    }
    public String getDesc(){return desc;}
}
