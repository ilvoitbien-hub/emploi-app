package com.emploiapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableMongoAuditing
@EnableScheduling
public class EmploiAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmploiAppApplication.class, args);
	}

}
