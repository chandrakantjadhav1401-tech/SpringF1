package com.classes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

 

public class Test {
	public static void main(String[] args) {
		//start the IOC 
		ApplicationContext context=new ClassPathXmlApplicationContext("chandu.xml");
		PaymentServices services= context.getBean(PaymentServices.class);
		services.doPayment(1000);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		 
		
	}

}
