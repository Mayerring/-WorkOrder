package com.example.spring_vue_demo.Interceptor;

import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.utils.StaffHolder;
import com.example.spring_vue_demo.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        log.info("token:{}", token);
        if (token != null && TokenUtil.verifyToken(token)) {
            Staff staff = TokenUtil.parsestaffFromToken(token);
            log.info("staff : {}", staff);
            StaffHolder.set(staff); // 存入 ThreadLocal
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        StaffHolder.clear(); // 清除 ThreadLocal 避免内存泄漏
    }
}
