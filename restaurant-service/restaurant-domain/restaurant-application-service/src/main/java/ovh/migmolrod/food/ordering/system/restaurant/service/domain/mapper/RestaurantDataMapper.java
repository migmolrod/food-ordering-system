package ovh.migmolrod.food.ordering.system.restaurant.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

	public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
		return Restaurant.builder()
				.restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
				.orderDetail(OrderDetail.builder()
						.orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
						.products(
								restaurantApprovalRequest
										.getProducts()
										.stream()
										.map(product -> Product.builder()
												.productId(product.getId())
												.quantity(product.getQuantity())
												.build())
										.collect(Collectors.toList()))
						.totalAmount(new Money(restaurantApprovalRequest.getPrice()))
						.orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
						.build())
				.build();
	}

	public OrderEventPayload approvalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
		return OrderEventPayload.builder()
				.orderId(orderApprovalEvent.getOrderApproval().getOrderId().toString())
				.restaurantId(orderApprovalEvent.getRestaurantId().toString())
				.createdAt(orderApprovalEvent.getCreatedAt())
				.approvalStatus(orderApprovalEvent.getOrderApproval().getApprovalStatus().name())
				.failureMessages(orderApprovalEvent.getFailureMessages())
				.build();
	}

}
