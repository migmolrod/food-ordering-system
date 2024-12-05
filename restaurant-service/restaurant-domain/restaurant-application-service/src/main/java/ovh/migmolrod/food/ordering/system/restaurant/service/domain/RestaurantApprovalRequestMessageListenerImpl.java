package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;

@Slf4j
@Service
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

	private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

	public RestaurantApprovalRequestMessageListenerImpl(
			RestaurantApprovalRequestHelper restaurantApprovalRequestHelper
	) {
		this.restaurantApprovalRequestHelper = restaurantApprovalRequestHelper;
	}

	@Override
	public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
		OrderApprovalEvent orderApprovalEvent;
		orderApprovalEvent = restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);
	}

}
