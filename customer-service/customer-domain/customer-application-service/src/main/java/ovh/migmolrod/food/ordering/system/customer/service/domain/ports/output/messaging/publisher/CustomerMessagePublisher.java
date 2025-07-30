package ovh.migmolrod.food.ordering.system.customer.service.domain.ports.output.messaging.publisher;

import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;

public interface CustomerMessagePublisher extends DomainEventPublisher<CustomerCreatedEvent> {}
