package UsingInterface;

public class UPIPayment implements IPayment {

	public void processPayment(double amount) {
		System.out.println("Payment is done using UPIPayment"+amount);
		
	}
}
