package com.darianngo.RiftCatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiftCatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiftCatcherApplication.class, args);
	}

}
