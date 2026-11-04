package com.hemobit.hemobit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HemobitApplication {

	public static void main(String[] args) {
		SpringApplication.run(HemobitApplication.class, args);
	}

}
