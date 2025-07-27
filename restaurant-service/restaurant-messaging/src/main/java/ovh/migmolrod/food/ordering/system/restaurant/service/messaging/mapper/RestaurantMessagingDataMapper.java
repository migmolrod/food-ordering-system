package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.ProductId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {

	public RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(
			RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel
	) {
		return RestaurantApprovalRequest.builder()
				.id(restaurantApprovalRequestAvroModel.getId())
				.sagaId(restaurantApprovalRequestAvroModel.getSagaId())
				.restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId())
				.orderId(restaurantApprovalRequestAvroModel.getOrderId())
				.restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()))
				.products(
						restaurantApprovalRequestAvroModel.getProducts().stream().map(
								product -> Product.builder()
										.productId(new ProductId(UUID.fromString(product.getId())))
										.quantity(product.getQuantity())
										.build()
						).collect(Collectors.toList())
				)
				.price(restaurantApprovalRequestAvroModel.getPrice())
				.createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
				.build();
	}

	public RestaurantApprovalResponseAvroModel orderEventPayloadToRestaurantApprovalResponseAvroModel(
			String sagaId,
			OrderEventPayload payload
	) {
		return RestaurantApprovalResponseAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId(sagaId)
				.setOrderId(payload.getOrderId())
				.setRestaurantId(payload.getRestaurantId())
				.setCreatedAt(payload.getCreatedAt().toInstant())
				.setOrderApprovalStatus(OrderApprovalStatus.valueOf(payload.getApprovalStatus()))
				.setFailureMessages(payload.getFailureMessages())
				.build();
	}

}
