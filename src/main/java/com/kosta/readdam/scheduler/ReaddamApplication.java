package com.kosta.readdam.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReaddamApplication {

	public static void main(String[] args) {
        SpringApplication.run(ReaddamApplication.class, args);
    }
	
}
