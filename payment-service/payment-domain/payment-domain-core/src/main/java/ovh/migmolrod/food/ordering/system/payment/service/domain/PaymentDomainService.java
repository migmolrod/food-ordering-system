package ovh.migmolrod.food.ordering.system.payment.service.domain;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditHistory;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

	PaymentEvent validateAndInitiatePayment(
			Payment payment,
			CreditEntry creditEntry,
			List<CreditHistory> creditHistories,
			List<String> failureMessages,
			DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
			DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
	);

	PaymentEvent validateAndCancelPayment(
			Payment payment,
			CreditEntry creditEntry,
			List<CreditHistory> creditHistories,
			List<String> failureMessages,
			DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
			DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
	);

}
