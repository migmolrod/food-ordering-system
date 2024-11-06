package ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
