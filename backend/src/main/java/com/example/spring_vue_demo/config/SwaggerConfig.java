package com.example.spring_vue_demo.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Configuration
@EnableKnife4j
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("工单系统API文档")
                        .version("1.0")
                        .description("工单系统API接口文档")
                        .contact(new Contact().name("wtt")))
                .externalDocs(new ExternalDocumentation()
                        .description("更多文档")
                        .url("https://example.com/docs"));
    }
}
