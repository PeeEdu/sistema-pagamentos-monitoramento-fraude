package com.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class TransferServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransferServiceApplication.class, args);
	}
}
