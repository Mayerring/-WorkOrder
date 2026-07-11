package com.example.workorder.cli.feign;

import com.example.workorder.api.dto.ValidateTokenResult;
import com.example.workorder.cli.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "workorder-backend", contextId = "auth", url = "${workorder.backend.url}", configuration = FeignConfig.class)
public interface AuthFeignClient {

    @GetMapping("/api/auth/validate")
    ResponseEntity<ValidateTokenResult> validateToken(
            @RequestHeader("Authorization") String token);
}