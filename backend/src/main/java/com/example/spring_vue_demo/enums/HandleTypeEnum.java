package com.example.spring_vue_demo.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Objects;

/**
 * @author wtt
 * @date 2025/06/02
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HandleTypeEnum {
    DISTRIBUTE(1,"分配"),
    APPLY_HELP(2,"请求协助"),
    URGE_ORDER(3,"催单"),
    FINISH(4,"完成"),
    CHECK_SUCCESS(5,"确认完成"),
    CHECK_FAILURE(6,"仍有问题"),
    CREATED(7,"创建成功"),
    ;

    private final Integer value;
    private final String desc;
    HandleTypeEnum(Integer value,String desc){
        this.value=value;
        this.desc=desc;
    }

    @JsonCreator
    public static HandleTypeEnum getByValue(Integer value){
        for(HandleTypeEnum typeEnum: HandleTypeEnum.values()){
            if(Objects.equals(typeEnum.value, value)){
                return typeEnum;
            }
        }
        return null;
    }


}
