package com.chanduandjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SbmsApiGetway3Application {

	public static void main(String[] args) {
		SpringApplication.run(SbmsApiGetway3Application.class, args);
	}

}
