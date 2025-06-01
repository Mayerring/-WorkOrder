package com.example.spring_vue_demo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.spring_vue_demo.mapper")
@SpringBootApplication
public class SpringVueDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringVueDemoApplication.class, args);
	}

}
