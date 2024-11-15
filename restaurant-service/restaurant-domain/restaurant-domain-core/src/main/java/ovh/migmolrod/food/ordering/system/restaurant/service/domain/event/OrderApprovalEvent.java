package ovh.migmolrod.food.ordering.system.restaurant.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.event.DomainEvent;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

	private final OrderApproval orderApproval;
	private final RestaurantId restaurantId;
	private final ZonedDateTime createdAt;
	private final List<String> failureMessages;

	public OrderApprovalEvent(
			OrderApproval orderApproval,
			RestaurantId restaurantId,
			ZonedDateTime createdAt,
			List<String> failureMessages
	) {
		this.orderApproval = orderApproval;
		this.restaurantId = restaurantId;
		this.createdAt = createdAt;
		this.failureMessages = failureMessages;
	}

	public OrderApproval getOrderApproval() {
		return orderApproval;
	}

	public RestaurantId getRestaurantId() {
		return restaurantId;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public List<String> getFailureMessages() {
		return failureMessages;
	}

}
