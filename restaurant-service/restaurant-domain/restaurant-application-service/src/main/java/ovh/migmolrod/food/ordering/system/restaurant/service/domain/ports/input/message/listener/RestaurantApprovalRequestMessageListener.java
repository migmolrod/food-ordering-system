package ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.input.message.listener;

import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {

	void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);

	void rejectOrder(RestaurantApprovalRequest restaurantApprovalRequest);

}
