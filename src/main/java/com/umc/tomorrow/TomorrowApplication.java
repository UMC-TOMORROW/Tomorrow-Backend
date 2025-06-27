package com.umc.tomorrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TomorrowApplication {

	public static void main(String[] args) {
		SpringApplication.run(TomorrowApplication.class, args);
	}

}
