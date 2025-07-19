package ovh.migmolrod.food.ordering.system.order.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

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

	public PaymentRequestAvroModel paymentEventPayloadToAvroModel(String sagaId, OrderPaymentEventPayload payload) {
		return PaymentRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId(sagaId)
				.setCustomerId(payload.getCustomerId())
				.setOrderId(payload.getOrderId())
				.setPrice(payload.getPrice())
				.setPaymentOrderStatus(PaymentOrderStatus.valueOf(payload.getPaymentOrderStatus()))
				.setCreatedAt(payload.getCreatedAt().toInstant())
				.build();
	}

	public RestaurantApprovalRequestAvroModel approvalEventPayloadToAvroModel(
			String sagaId,
			OrderApprovalEventPayload payload
	) {
		return RestaurantApprovalRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId(sagaId)
				.setOrderId(payload.getOrderId())
				.setRestaurantId(payload.getRestaurantId())
				.setPrice(payload.getPrice())
				.setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(payload.getRestaurantOrderStatus()))
				.setProducts(payload.getProducts().stream().map(this::productToAvroModel).collect(Collectors.toList()))
				.setCreatedAt(payload.getCreatedAt().toInstant())
				.build();
	}

	private Product productToAvroModel(OrderApprovalEventProduct product) {
		return Product.newBuilder()
				.setId(product.getId())
				.setQuantity(product.getQuantity())
				.build();
	}

}
