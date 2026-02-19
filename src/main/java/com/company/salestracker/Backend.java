package com.company.salestracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.mail.MessagingException;

@SpringBootApplication
@EnableJpaAuditing
public class Backend {

	
	public static void main(String[] args) throws MessagingException {
        SpringApplication.run(Backend.class, args);

	}

}
