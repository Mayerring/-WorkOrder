package com.example.spring_vue_demo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.spring_vue_demo.mapper")
@SpringBootApplication
@EnableScheduling
public class SpringVueDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringVueDemoApplication.class, args);
	}

}
