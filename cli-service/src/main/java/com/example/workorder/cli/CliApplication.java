package com.example.workorder.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CliApplication {

    public static void main(String[] args) {
        SpringApplication.run(CliApplication.class, args);
    }
}