package ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymenteCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
