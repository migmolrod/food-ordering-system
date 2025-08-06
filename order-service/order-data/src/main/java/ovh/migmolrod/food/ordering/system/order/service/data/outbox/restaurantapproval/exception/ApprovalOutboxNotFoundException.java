package ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException {

	public ApprovalOutboxNotFoundException(String message) {
		super(message);
	}

}
