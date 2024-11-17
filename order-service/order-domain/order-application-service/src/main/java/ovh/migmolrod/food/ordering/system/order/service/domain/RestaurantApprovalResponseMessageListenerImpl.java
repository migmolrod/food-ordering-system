package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import ovh.migmolrod.food.ordering.system.order.service.domain.saga.OrderApprovalSaga;

import static ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {

	private final OrderApprovalSaga orderApprovalSaga;

	public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
		this.orderApprovalSaga = orderApprovalSaga;
	}

	@Override
	public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
		orderApprovalSaga.process(restaurantApprovalResponse);
		log.info("Order is approved for order id {}", restaurantApprovalResponse.getOrderId());
	}

	@Override
	public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
		OrderCancelledEvent event = orderApprovalSaga.rollback(restaurantApprovalResponse);
		log.info(
				"Publishing OrderCancelledEvent for order id {}. Failure messages {}",
				restaurantApprovalResponse.getOrderId(),
				String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.getFailureMessages())
		);
		event.fire();
	}

}
