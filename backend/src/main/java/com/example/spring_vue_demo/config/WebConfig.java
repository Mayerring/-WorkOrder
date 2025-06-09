package com.example.spring_vue_demo.config;

import com.example.spring_vue_demo.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                //.excludePathPatterns("/**");//先放行所有接口
                .excludePathPatterns("/user/login") // 登录接口放行
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v3/**", "/doc.html/**")//swagger放行
//                .excludePathPatterns("/**");//先放行所有接口
        ;
    }
    /**
     * 放行swagger-ui静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
