package com.example.spring_vue_demo.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author wtt
 * @date 2025/06/05
 */
@Getter
public enum TimeTypeEnum {
    WEEK(1),
    MONTH(2),
    YEAR(3)
    ;
    private final int value;
    TimeTypeEnum(int value){
        this.value=value;
    }
    public static TimeTypeEnum getByValue(Integer value){
        for(TimeTypeEnum typeEnum: TimeTypeEnum.values()){
            if(Objects.equals(typeEnum.value, value)){
                return typeEnum;
            }
        }
        return null;
    }
}
