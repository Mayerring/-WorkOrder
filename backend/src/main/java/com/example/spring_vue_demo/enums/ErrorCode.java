package com.example.spring_vue_demo.enums;

/**
 * @author wtt
 * @date 2025/06/01
 */
public enum ErrorCode {
    ID_AND_CODE_IS_NULL("id和code不能都为空!",10000),
    WORK_ORDER_NOT_EXIST("该工单id或编号不存在!",10001),
    FINISH_STATUS_WRONG("处理中、延时的工单才能完成",10002)
    ;
    private final String msg;
    private final Integer code;
    ErrorCode(String msg,Integer code){
        this.msg=msg;
        this.code=code;
    }
    public String getMsg(){return msg;}
    public Integer getCode(){return code;}
}
