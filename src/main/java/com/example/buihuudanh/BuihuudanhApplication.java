package com.example.buihuudanh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.example.buihuudanh", "com.example.entity" })
@EntityScan("com.example.entity")
public class BuihuudanhApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuihuudanhApplication.class, args);
	}

}
