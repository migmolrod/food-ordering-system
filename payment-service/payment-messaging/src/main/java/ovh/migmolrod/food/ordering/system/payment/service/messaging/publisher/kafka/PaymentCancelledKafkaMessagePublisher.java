package ovh.migmolrod.food.ordering.system.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;

@Slf4j
@Component
public class PaymentCancelledKafkaMessagePublisher implements PaymentCancelledMessagePublisher {

	private final PaymentMessagingDataMapper paymentMessagingDataMapper;
	private final PaymentServiceConfigData paymentServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;

	public PaymentCancelledKafkaMessagePublisher(
			PaymentMessagingDataMapper paymentMessagingDataMapper,
			PaymentServiceConfigData paymentServiceConfigData,
			KafkaMessageHelper kafkaMessageHelper,
			KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer
	) {
		this.paymentMessagingDataMapper = paymentMessagingDataMapper;
		this.paymentServiceConfigData = paymentServiceConfigData;
		this.kafkaMessageHelper = kafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(PaymentCancelledEvent domainEvent) {
		String orderId = domainEvent.getPayment().getOrderId().getValue().toString();
		log.info("Received PaymentCancelledEvent for order id: {}", orderId);

		try {
			PaymentResponseAvroModel paymentResponseAvroModel =
					paymentMessagingDataMapper.paymentCancelledEventToPaymentResponseAvroModel(domainEvent);

			kafkaProducer.send(
					paymentServiceConfigData.getPaymentResponseTopicName(),
					orderId,
					paymentResponseAvroModel,
					kafkaMessageHelper.getKafkaCallback(
							paymentServiceConfigData.getPaymentResponseTopicName(),
							paymentResponseAvroModel,
							orderId,
							"PaymentResponseAvroModel"
					)
			);

			log.info("PaymentResponseAvroModel sent to kafka for order id: {}", orderId);
		} catch (Exception e) {
			log.error("""
					Error while sending PaymentCancelledEvent message to kafka with order id: {}, error: {}",
					""", orderId, e.getMessage(), e);
		}
	}

}