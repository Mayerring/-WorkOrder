package com.example.spring_vue_demo.utils;

import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.info("authHeader: " + authHeader);
        if (StringUtils.hasText(authHeader)) {
            //String token = authHeader.substring(7);
            String token = authHeader;
            log.info("token: " + token);
            log.info(""+TokenUtil.verifyToken(token));
            if (TokenUtil.verifyToken(token)) {
                Staff staff = TokenUtil.parsestaffFromToken(token);
                log.info("staff : {}", staff);
                // 权限设置：用 role 作为 authority，前缀为 ROLE_
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                staff,
                                null,
                                Collections.singleton(() -> "ROLE_" + staff.getRole().toUpperCase())
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置当前线程的认证对象
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
