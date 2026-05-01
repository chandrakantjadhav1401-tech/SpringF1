package com.classes;



//the example of tight coupling classes 
//now a am calling another method in using credit card that time create the tight coupling
//limited options 

public class Test {
	public static void main(String[] args) {
		//constructor Injection 
//		PaymentServices pServices=new PaymentServices(new UPIPayment(),new DebitCard());
//		pServices.debitCard=new DebitCard();
//		pServices.upiPayment=new UPIPayment();
//		pServices.doPayment(1000);
		
		
		//this is called setter Injection 
		PaymentServices ps=new PaymentServices();
		ps.setPayment(new UPIPayment(), new DebitCard());
		ps.doPayment(1000);
		
		
	}

}
