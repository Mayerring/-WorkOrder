package com.example.spring_vue_demo.enums;

import lombok.Getter;

/**
 * @author wtt
 * @date 2025/06/01
 */
@Getter
public enum ErrorCode {
    //工单管理错误信息
    ID_AND_CODE_IS_NULL("id和code不能都为空!",10000),
    WORK_ORDER_NOT_EXIST("该工单id或编号不存在!",10001),
    DISTRIBUTE_UNDISTRIBUTED_STATUS_WRONG("未分配的工单才能分配",10002),
    FINISH_ING_STATUS_WRONG("处理中、延时的工单才能完成",10003),
    URGE_ING_STATUS_WRONG("处理中、延时的工单才能催单",10004),
    HELP_ING_STATUS_WRONG("处理中、延时的工单才能申请协助",10005),
    CHECK_FINISHED_STATUS_WRONG("已完成的工单才能确认完成",10006),
    HELP_REQUIRE_ASSIGNED_USER_ID("请求协助操作，协助人不能为空",10007),
    DISTRIBUTE_REQUIRE_ASSIGNED_USER_ID("分配工单操作，分配人不能为空",10008),
    CURRENT_USER_IS_NOT_HANDLE_USER("此工单当前状态处理人不是该用户",10009),
    ASSIGNED_USER_EQUALS_CURRENT_USER("用户协助对象不能为自己",10010),
    NOT_NEED_ASSIGNED_USER_ID("该操作不需要填写安排用户id",10011),
    ASSIGNED_USER_ALREADY_EXIST("安排的用户已经被执行过相同操作",10012),
    AFTER_FINISHED_NOT_ALLOW_CANCELLED("已完成、已确认完成的工单不能取消",10013),
    ONLY_SUBMIT_USER_CAN_CANCEL_WORK_ORDER("只有提交的用户可以取消工单",10014),
    AUDIT_ING_STATUS_WRONG("只有待审核和审核中的工单才能进行审核操作",10015),
    UNAUDITED_AUDITING_STATUS_WRONG("待审核、审核中工单才能审核",10016 ),

    //流程错误信息
    EXIST_NODE_HANDLE_TYPE_WRONG("存在节点处理类型传参错误",10100),
    ONLY_ONE_DISTRIBUTER_IN_A_FLOW("一个流程中只能有一个分配人",10101),
    ONLY_ONE_CHECKER_IN_A_FLOW("一个流程中只能有一个验收人",10102),
    FLOW_ERROR_CURRENT_HANDLER_IS_WRONG("当前用户没查询到对应流程节点",10103),
    FLOW_ERROR_NEXT_NODE_IS_NULL("当前用户没查询到下一个流程节点", 10104),
    AUDIT_HANDLER_CAN_NOT_REPEAT("审核操作人不允许重复",10105),
    FLOW_NOT_EXIST("流程不存在",10106 );
    private final String msg;
    private final Integer code;
    ErrorCode(String msg,Integer code){
        this.msg=msg;
        this.code=code;
    }
}
