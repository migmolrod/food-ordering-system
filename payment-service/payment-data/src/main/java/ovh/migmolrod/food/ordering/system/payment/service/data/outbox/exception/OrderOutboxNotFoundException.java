package ovh.migmolrod.food.ordering.system.payment.service.data.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException {

	public OrderOutboxNotFoundException(String message) {
		super(message);
	}

}
