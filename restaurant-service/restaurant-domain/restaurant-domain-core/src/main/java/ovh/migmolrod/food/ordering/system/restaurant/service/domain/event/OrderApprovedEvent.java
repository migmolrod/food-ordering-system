package ovh.migmolrod.food.ordering.system.restaurant.service.domain.event;

import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {

	public OrderApprovedEvent(
			OrderApproval orderApproval,
			RestaurantId restaurantId,
			ZonedDateTime createdAt,
			List<String> failureMessages
	) {
		super(orderApproval, restaurantId, createdAt, failureMessages);
	}

}
