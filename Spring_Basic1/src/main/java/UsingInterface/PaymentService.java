package UsingInterface;

 
public class PaymentService {

	IPayment payment;
//	implemented classes 3 
	
	public PaymentService(IPayment payment) {
		this.payment=payment;
		System.out.println("constructor");
	}
	
//	public void setPayment(IPayment payment)
//	{
//		System.out.println("setter called");
//		this.payment=payment;	
//	}
	
	public void doPayment(double amount)
	{
		payment.processPayment(amount);
		System.out.println("Payment is success"+amount);
	}
	
	
}
