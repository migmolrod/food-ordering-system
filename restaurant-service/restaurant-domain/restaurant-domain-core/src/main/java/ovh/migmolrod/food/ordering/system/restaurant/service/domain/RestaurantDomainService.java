package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {

	OrderApprovalEvent validateOrder(
			Restaurant restaurant,
			List<String> failureMessages
	);

}
