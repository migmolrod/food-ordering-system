package ovh.migmolrod.food.ordering.system.order.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

	private final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;

	public OrderPaidEvent(
			Order order,
			ZonedDateTime createdAt,
			DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher
	) {
		super(order, createdAt);
		this.orderPaidEventDomainEventPublisher = orderPaidEventDomainEventPublisher;
	}

	@Override
	public void fire() {
		orderPaidEventDomainEventPublisher.publish(this);
	}

}
