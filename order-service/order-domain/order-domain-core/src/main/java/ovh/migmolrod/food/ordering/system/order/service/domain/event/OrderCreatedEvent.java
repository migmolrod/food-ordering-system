package ovh.migmolrod.food.ordering.system.order.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvent {

	private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher;

	public OrderCreatedEvent(
			Order order,
			ZonedDateTime createdAt,
			DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher
	) {
		super(order, createdAt);
		this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
	}

	@Override
	public void fire() {
		orderCreatedEventDomainEventPublisher.publish(this);
	}

}
