package ovh.migmolrod.food.ordering.system.restaurant.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

	private final DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher;

	public OrderRejectedEvent(
			OrderApproval orderApproval,
			RestaurantId restaurantId,
			ZonedDateTime createdAt,
			List<String> failureMessages,
			DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher
	) {
		super(orderApproval, restaurantId, createdAt, failureMessages);
		this.orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher;
	}

	@Override
	public void fire() {
		orderRejectedEventDomainEventPublisher.publish(this);
	}

}
