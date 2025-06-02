package com.example.spring_vue_demo.enums;

/**
 * @author wtt
 * @date 2025/06/01
 */
public enum ErrorCode {
    ID_AND_CODE_IS_NULL("id和code不能都为空!",10000),
    WORK_ORDER_NOT_EXIST("该工单id或编号不存在!",10001),
    DISTRIBUTE_UNDISTRIBUTED_STATUS_WRONG("未分配的工单才能分配",10002),
    FINISH_ING_STATUS_WRONG("处理中、延时的工单才能完成",10003),
    URGE_ING_STATUS_WRONG("处理中、延时的工单才能催单",10004),
    HELP_ING_STATUS_WRONG("处理中、延时的工单才能申请协助",10005),
    CHECK_FINISHED_STATUS_WRONG("已完成的工单才能确认完成",10006),
    HELP_REQUIRE_ASSIGNED_USER_ID("请求协助操作，协助人不能为空",10007),
    DISTRIBUTE_REQUIRE_ASSIGNED_USER_ID("分配工单操作，分配人不能为空",1008),
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
