package ovh.migmolrod.food.ordering.system.restaurant.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.UUID;

@Component
public class RestaurantDataMapper {

	public OrderApproval restaurantApprovalREquestToOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
		return OrderApproval.builder()
				.orderApprovalId(new OrderApprovalId(UUID.fromString(restaurantApprovalRequest.getId())))
				.restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
				.orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
				.build();
	}

}
