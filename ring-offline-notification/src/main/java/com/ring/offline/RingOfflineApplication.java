package com.ring.offline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ring.offline.config.RingOfflineProperties;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ring.offline.repository")
@EnableConfigurationProperties(RingOfflineProperties.class)
@EnableScheduling
public class RingOfflineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RingOfflineApplication.class, args);
	}

}
