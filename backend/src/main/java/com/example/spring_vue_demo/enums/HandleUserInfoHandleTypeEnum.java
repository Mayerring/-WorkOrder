package com.example.spring_vue_demo.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author wtt
 * @date 2025/05/31
 */
@Getter
public enum HandleUserInfoHandleTypeEnum {
    SUBMIT(1,"提交"),
    AUDIT(2,"审核"),
    DISTRIBUTE(3,"派单"),
    HANDLE(4,"处理"),
    CHECK(5,"确认")
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

}
