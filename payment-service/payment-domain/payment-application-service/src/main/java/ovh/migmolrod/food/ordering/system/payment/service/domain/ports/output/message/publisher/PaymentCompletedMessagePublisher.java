package ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;

public interface PaymentCompletedMessagePublisher extends DomainEventPublisher<PaymentCompletedEvent> {
}
