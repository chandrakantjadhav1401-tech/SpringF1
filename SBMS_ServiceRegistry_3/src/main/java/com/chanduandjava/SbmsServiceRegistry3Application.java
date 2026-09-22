package com.chanduandjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SbmsServiceRegistry3Application {

	public static void main(String[] args) {
		SpringApplication.run(SbmsServiceRegistry3Application.class, args);
	}

}
