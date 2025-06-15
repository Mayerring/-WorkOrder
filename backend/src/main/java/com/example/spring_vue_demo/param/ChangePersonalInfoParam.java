package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePersonalInfoParam {
    @Schema(description = "密码")
    private String password;
    @Schema(description = "手机号码")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
}
