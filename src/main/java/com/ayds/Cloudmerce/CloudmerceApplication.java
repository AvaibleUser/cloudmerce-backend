package com.ayds.Cloudmerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("file:${user.dir}/.env")
@SpringBootApplication
public class CloudmerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudmerceApplication.class, args);
	}

}
