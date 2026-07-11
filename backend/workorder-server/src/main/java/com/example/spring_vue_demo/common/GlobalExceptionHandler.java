package com.example.spring_vue_demo.common;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.enums.ErrorCode;
import com.example.spring_vue_demo.exception.UserSideException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author wtt
 * @date 2025/06/01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserSideException.class)
    public Result handleUserSideException(UserSideException e) {
        ErrorCode errorCode = e.getErrorCode();
        return Result.error(errorCode.getCode(), errorCode.getMsg());
    }
}

