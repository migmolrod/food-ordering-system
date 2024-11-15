package ovh.migmolrod.food.ordering.system.restaurant.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {

	private final DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;

	public OrderApprovedEvent(
			OrderApproval orderApproval,
			RestaurantId restaurantId,
			ZonedDateTime createdAt,
			List<String> failureMessages,
			DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher
	) {
		super(orderApproval, restaurantId, createdAt, failureMessages);
		this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
	}

	@Override
	public void fire() {
		orderApprovedEventDomainEventPublisher.publish(this);
	}

}
