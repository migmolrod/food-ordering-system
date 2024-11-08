package ovh.migmolrod.food.ordering.system.payment.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

	public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(
			PaymentCompletedEvent paymentCompletedEvent
	) {
		return getPaymentResponseAvroModel(paymentCompletedEvent);
	}

	public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(
			PaymentCancelledEvent paymentCancelledEvent
	) {
		return getPaymentResponseAvroModel(paymentCancelledEvent);
	}

	public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(
			PaymentFailedEvent paymentFailedEvent
	) {
		return getPaymentResponseAvroModel(paymentFailedEvent);
	}

	public PaymentRequest paymentRequestAvroModelToPaymentRequest(
			PaymentRequestAvroModel paymentRequestAvroModel
	) {
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

	private PaymentResponseAvroModel getPaymentResponseAvroModel(
			PaymentEvent event
	) {
		Payment payment = event.getPayment();

		return PaymentResponseAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setPaymentId(payment.getId().getValue().toString())
				.setCustomerId(payment.getCustomerId().getValue().toString())
				.setOrderId(payment.getOrderId().getValue().toString())
				.setPrice(payment.getPrice().getAmount())
				.setCreatedAt(payment.getCreatedAt().toInstant())
				.setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
				.setFailureMessages(event.getFailureMessages())
				.build();
	}

}
