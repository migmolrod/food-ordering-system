package ovh.migmolrod.food.ordering.system.payment.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {

	private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

	public PaymentCompletedEvent(
			Payment payment,
			ZonedDateTime createdAt,
			DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher
	) {
		super(payment, createdAt, Collections.emptyList());
		this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
	}

	@Override
	public void fire() {
		paymentCompletedEventDomainEventPublisher.publish(this);
	}

}
