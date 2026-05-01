package com.classes;

public class PaymentServices {
	
	
	 
	
	
	
	
	
	UPIPayment upiPayment;
	
	DebitCard debitCard;

//	public PaymentServices(UPIPayment upiPayment,DebitCard debitCard) {
//		this.upiPayment=upiPayment;
//		this.debitCard=debitCard;
//		
//	}
	
	public void setPayment(UPIPayment upiPayment,DebitCard debitCard)
	{
		this.upiPayment=upiPayment;
		this.debitCard=debitCard;
	}
	
	
	//when a am calling the method not create the static or not create the object 
	//then who to call we go to extends techniques 
	 public void doPayment(double amount)
	 {
		 //super.UPIPayment(amount);
		 //null.UPIPayment(amount);
		 upiPayment.UPIPayment(amount);
		 debitCard.debitCard(amount);
		 
		 System.out.println("Payment success is"+amount);
		 
	 }
	 
	 

}
