package com.example.webIA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebIaApplication.class, args);
	}

}
