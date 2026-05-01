package UsingInterface;

public class CreaditCard implements IPayment {
	
	@Override
	public void processPayment(double amount) {
		System.out.println("Payment is done using Credit card"+amount);
		
	}
	

}
