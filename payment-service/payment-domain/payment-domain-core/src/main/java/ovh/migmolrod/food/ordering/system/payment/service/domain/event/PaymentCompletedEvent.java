package ovh.migmolrod.food.ordering.system.payment.service.domain.event;

import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentCompletedEvent extends PaymentEvent {

	protected PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages) {
		super(payment, createdAt, failureMessages);
	}

}
