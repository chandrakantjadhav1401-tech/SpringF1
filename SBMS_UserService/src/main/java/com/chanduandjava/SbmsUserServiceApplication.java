package com.chanduandjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
 

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chanduandjava.extenral.services")
public class SbmsUserServiceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(SbmsUserServiceApplication.class, args);
	}

}
