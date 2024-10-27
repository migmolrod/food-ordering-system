package ovh.migmolrod.food.ordering.system.order.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.DomainEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public abstract class OrderEvent implements DomainEvent<Order> {

	private final Order order;
	private final ZonedDateTime createdAt;

	public OrderEvent(Order order, ZonedDateTime createdAt) {
		this.order = order;
		this.createdAt = createdAt;
	}

	public Order getOrder() {
		return order;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

}
