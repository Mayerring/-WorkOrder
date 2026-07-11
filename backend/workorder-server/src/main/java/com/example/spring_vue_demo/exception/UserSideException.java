package com.example.spring_vue_demo.exception;

import com.example.spring_vue_demo.enums.ErrorCode;

/**
 * @author wtt
 * @date 2025/06/01
 */
public class UserSideException extends RuntimeException{
    private ErrorCode errorCode;

    public UserSideException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode=errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
