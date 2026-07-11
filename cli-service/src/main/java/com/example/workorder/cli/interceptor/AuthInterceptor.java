package com.example.workorder.cli.interceptor;

import com.example.workorder.api.dto.ValidateTokenResult;
import com.example.workorder.cli.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String STAFF_INFO_ATTR = "staffInfo";

    @Autowired
    @Lazy
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authHeader)) {
            log.warn("Missing Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Missing Authorization header\",\"data\":null}");
            return false;
        }

        ValidateTokenResult result = authService.validateToken(authHeader);
        if (!result.isValid()) {
            log.warn("Invalid token: {}", authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"" + result.getMessage() + "\",\"data\":null}");
            return false;
        }

        request.setAttribute(STAFF_INFO_ATTR, result);
        return true;
    }
}