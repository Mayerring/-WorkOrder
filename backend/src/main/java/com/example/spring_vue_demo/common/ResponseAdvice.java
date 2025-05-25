package com.example.spring_vue_demo.common;

import com.alibaba.fastjson.JSON;
import com.example.spring_vue_demo.entity.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;



/**
 * @author wtt
 * @date 2025/05/24
 */
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 获取当前请求路径
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String requestURI = attributes.getRequest().getRequestURI();

            // 如果是 Knife4j 相关路径，则不拦截
            if (requestURI.startsWith("/doc.html")
                    || requestURI.startsWith("/webjars/")
                    || requestURI.startsWith("/v3/api-docs")
                    || requestURI.startsWith("/swagger-resources")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 如果已经是Result类型，直接返回
        if (body instanceof Result) {
            return body;
        }

        // 处理String类型特殊处理
        if (body instanceof String) {
            return JSON.toJSONString(Result.success(body));
        }

        // 其他类型统一包装
        return Result.success(body);
    }
}
