package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginParam {
    @Schema(description = "工号")
    private String staffNumber;
    @Schema(description = "手机号码")
    private String phone;
    @NotNull
    @Schema(description = "密码")
    private String password;
}
