package com.classes;

public class PaymentServices {	
	
	
//	but in case we have 50 classes then we write the 50 class reference variable 
//	that is this not good way..
    DebitCard debitCard;
    
    UPIPayment upi;
//    when we are going to create the separate the constructor that time give the exception
//    then it will add both in one 
    
    

    // ✅ ADD THIS CONSTRUCTOR
    public PaymentServices(DebitCard debitCard,UPIPayment upi) {
        this.debitCard = debitCard;
        this.upi=upi;
        
    }
    
    
//    public PaymentServices(UPIPayment upi) {
//    	this.upi=upi;
//		
//	}

    public void doPayment(double amount) {
        debitCard.debitCard(amount);
        upi.UPIPayment(amount);
        System.out.println("Payment success is " + amount);
    }
}