package ovh.migmolrod.food.ordering.system.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

	private final PaymentResponseMessageListener paymentResponseKafkaMessageListener;
	private final OrderMessagingDataMapper orderMessagingDataMapper;

	public PaymentResponseKafkaListener(
			PaymentResponseMessageListener paymentResponseKafkaMessageListener,
			OrderMessagingDataMapper orderMessagingDataMapper
	) {
		this.paymentResponseKafkaMessageListener = paymentResponseKafkaMessageListener;
		this.orderMessagingDataMapper = orderMessagingDataMapper;
	}

	@Override
	@KafkaListener(
			id = "${kafka-consumer-config.payment-consumer-group-id}",
			topics = "${order-service.payment-response-topic-name}"
	)
	public void receive(
			@Payload List<PaymentResponseAvroModel> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets
	) {
		log.info("""
						{} payment responses received with keys {}, partitions {} and offsets {}
						""",
				messages.size(),
				keys.toString(),
				partitions.toString(),
				offsets.toString()
		);

		messages.forEach(paymentResponseAvroModel -> {
			if (PaymentStatus.COMPLETED.equals(paymentResponseAvroModel.getPaymentStatus())) {
				log.info("Processing successful payment for order id: {}", paymentResponseAvroModel.getOrderId());
				paymentResponseKafkaMessageListener.paymentCompleted(
						orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel)
				);
			} else if (List.of(PaymentStatus.CANCELLED, PaymentStatus.FAILED).contains(paymentResponseAvroModel.getPaymentStatus())) {
				log.info("Processing unsuccessful payment for order id: {}", paymentResponseAvroModel.getOrderId());
				paymentResponseKafkaMessageListener.paymentCancelled(
						orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel)
				);
			}
		});
	}

}
