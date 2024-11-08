package ovh.migmolrod.food.ordering.system.order.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

	public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(
			OrderCreatedEvent orderCreatedEvent
	) {
		Order order = orderCreatedEvent.getOrder();

		return PaymentRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setCustomerId(order.getCustomerId().getValue().toString())
				.setOrderId(order.getId().getValue().toString())
				.setPrice(order.getPrice().getAmount())
				.setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
				.setPaymentOrderStatus(PaymentOrderStatus.PENDING)
				.build();
	}


	public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(
			OrderCancelledEvent orderCancelledEvent
	) {
		Order order = orderCancelledEvent.getOrder();

		return PaymentRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setCustomerId(order.getCustomerId().getValue().toString())
				.setOrderId(order.getId().getValue().toString())
				.setPrice(order.getPrice().getAmount())
				.setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
				.setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
				.build();
	}

	public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(
			OrderPaidEvent orderPaidEvent
	) {
		Order order = orderPaidEvent.getOrder();

		return RestaurantApprovalRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setOrderId(order.getId().getValue().toString())
				.setRestaurantId(order.getRestaurantId().getValue().toString())
				.setPrice(order.getPrice().getAmount())
				.setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
				.setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
				.setProducts(order.getItems().stream().map(orderItem ->
								Product.newBuilder()
										.setId(orderItem.getProduct().getId().getValue().toString())
										.setQuantity(orderItem.getQuantity())
										.build()
						).toList()
				)
				.build();
	}

	public PaymentResponse paymentResponseAvroModelToPaymentResponse(
			PaymentResponseAvroModel paymentResponseAvroModel
	) {
		return PaymentResponse.builder()
				.id(paymentResponseAvroModel.getId())
				.sagaId(paymentResponseAvroModel.getSagaId())
				.paymentId(paymentResponseAvroModel.getPaymentId())
				.customerId(paymentResponseAvroModel.getCustomerId())
				.orderId(paymentResponseAvroModel.getOrderId())
				.price(paymentResponseAvroModel.getPrice())
				.createdAt(paymentResponseAvroModel.getCreatedAt())
				.paymentStatus(PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().name()))
				.failureMessages(paymentResponseAvroModel.getFailureMessages())
				.build();
	}

	public RestaurantApprovalResponse restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
			RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel
	) {
		return RestaurantApprovalResponse.builder()
				.id(restaurantApprovalResponseAvroModel.getId())
				.sagaId(restaurantApprovalResponseAvroModel.getSagaId())
				.restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
				.orderId(restaurantApprovalResponseAvroModel.getOrderId())
				.createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
				.orderApprovalStatus(OrderApprovalStatus.valueOf(restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name()))
				.failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
				.build();
	}

}
