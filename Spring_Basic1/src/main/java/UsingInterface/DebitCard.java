package UsingInterface;

public class DebitCard implements IPayment {
	
	@Override
	public void processPayment(double amount) {
		System.out.println("Payment is done using Debit Card"+amount);
		
	}

}
