package com.example.workorder.cli.feign;

import com.example.workorder.api.param.MessageParam;
import com.example.workorder.cli.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "workorder-backend", contextId = "dashboard", url = "${workorder.backend.url}", configuration = FeignConfig.class)
public interface DashboardFeignClient {

    @PostMapping("/dashboard/data")
    ResponseEntity<?> getData(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId);

    @PostMapping("/dashboard/handleQuantity")
    ResponseEntity<?> getHandleQuantity(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId);

    @PostMapping("/dashboard/pageMessages")
    ResponseEntity<?> pageMessages(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Trace-ID") String traceId,
            @RequestBody MessageParam param);
}