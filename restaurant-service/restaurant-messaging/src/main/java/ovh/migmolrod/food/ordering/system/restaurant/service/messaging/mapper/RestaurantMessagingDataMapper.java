package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.ProductId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {

	public RestaurantApprovalResponseAvroModel orderApprovedEventToRestaurantApprovalResponseAvroModel(
			OrderApprovedEvent orderApprovedEvent
	) {
		return RestaurantApprovalResponseAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue().toString())
				.setRestaurantId(orderApprovedEvent.getRestaurantId().getValue().toString())
				.setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
				.setOrderApprovalStatus(
						OrderApprovalStatus.valueOf(orderApprovedEvent.getOrderApproval().getApprovalStatus().name())
				)
				.setFailureMessages(orderApprovedEvent.getFailureMessages())
				.build();
	}

	public RestaurantApprovalResponseAvroModel orderRejectedEventToRestaurantApprovalResponseAvroModel(
			OrderRejectedEvent orderRejectedEvent
	) {
		return RestaurantApprovalResponseAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString())
				.setRestaurantId(orderRejectedEvent.getRestaurantId().getValue().toString())
				.setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
				.setOrderApprovalStatus(
						OrderApprovalStatus.valueOf(orderRejectedEvent.getOrderApproval().getApprovalStatus().name())
				)
				.setFailureMessages(orderRejectedEvent.getFailureMessages())
				.build();
	}

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

}
