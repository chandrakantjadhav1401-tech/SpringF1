package UsingInterface;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
	
	public static void main(String[] args) {
		
		ApplicationContext context=new ClassPathXmlApplicationContext("chandu1.xml");
		PaymentService service= context.getBean(PaymentService.class);
		service.doPayment(1000);
		
	}
 
}
