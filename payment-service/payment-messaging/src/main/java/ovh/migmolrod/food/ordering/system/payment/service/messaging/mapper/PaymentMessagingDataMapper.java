package ovh.migmolrod.food.ordering.system.payment.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

	public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
		return PaymentRequest.builder()
				.id(paymentRequestAvroModel.getId())
				.sagaId(paymentRequestAvroModel.getSagaId())
				.customerId(paymentRequestAvroModel.getCustomerId())
				.orderId(paymentRequestAvroModel.getOrderId())
				.price(paymentRequestAvroModel.getPrice())
				.createdAt(paymentRequestAvroModel.getCreatedAt())
				.paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
				.build();
	}

	public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId, OrderEventPayload payload) {
		return PaymentResponseAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId(sagaId)
				.setPaymentId(payload.getPaymentId())
				.setCustomerId(payload.getCustomerId())
				.setOrderId(payload.getOrderId())
				.setPrice(payload.getPrice())
				.setCreatedAt(payload.getCreatedAt().toInstant())
				.setPaymentStatus(PaymentStatus.valueOf(payload.getPaymentStatus()))
				.setFailureMessages(payload.getFailureMessages())
				.build();
	}

}
