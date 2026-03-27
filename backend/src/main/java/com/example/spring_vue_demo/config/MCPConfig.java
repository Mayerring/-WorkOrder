 package com.example.spring_vue_demo.config;


 import com.example.spring_vue_demo.tool.AssistantTools;
 import org.springframework.ai.tool.ToolCallbackProvider;
 import org.springframework.ai.tool.method.MethodToolCallbackProvider;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;


/**
  * @author wtt
  * @date 2025/08/24
  */
 @Configuration
 public class MCPConfig {
    @Autowired
    private AssistantTools assistantTools;

    @Bean
    public ToolCallbackProvider workOrderTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(assistantTools)
                .build();
    }
}
