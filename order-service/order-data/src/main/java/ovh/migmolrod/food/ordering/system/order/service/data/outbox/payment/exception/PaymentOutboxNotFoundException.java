package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.exception;

public class PaymentOutboxNotFoundException extends RuntimeException {
	public PaymentOutboxNotFoundException(String message) {
		super(message);
	}
}
