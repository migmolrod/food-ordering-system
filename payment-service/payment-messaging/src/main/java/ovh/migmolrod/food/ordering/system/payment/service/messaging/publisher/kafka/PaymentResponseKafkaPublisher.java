package ovh.migmolrod.food.ordering.system.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class PaymentResponseKafkaPublisher implements PaymentResponseMessagePublisher {

	private final PaymentMessagingDataMapper paymentMessagingDataMapper;
	private final PaymentServiceConfigData paymentServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;

	public PaymentResponseKafkaPublisher(
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
	public void publish(OrderOutboxMessage outboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
		OrderEventPayload payload = this.kafkaMessageHelper.createOrderEventPayload(
				outboxMessage.getPayload(),
				OrderEventPayload.class
		);
		String sagaId = outboxMessage.getSagaId().toString();
		String orderId = payload.getOrderId();
		log.info("Received OrderOutboxMessage for order id '{}' and saga id '{}'", orderId, sagaId);

		try {
			PaymentResponseAvroModel avroModel =
					this.paymentMessagingDataMapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, payload);

			String topicName = this.paymentServiceConfigData.getPaymentResponseTopicName();

			ListenableFutureCallback<SendResult<String, PaymentResponseAvroModel>> kafkaCallback =
					this.kafkaMessageHelper.getKafkaCallback(
							topicName,
							avroModel,
							outboxMessage,
							outboxCallback,
							orderId,
							"PaymentResponseAvroModel"
					);

			this.kafkaProducer.send(topicName, sagaId, avroModel, kafkaCallback);

			log.info("PaymentResponseAvroModel sent to kafka for order id '{}' and saga id '{}'", orderId, sagaId);
		} catch (Exception e) {
			String errorMessage = String.format("""
					Could not send OrderOutboxMessage to Kafka for order id '%s' and saga id '%s'
					""", orderId, sagaId);
			log.error(errorMessage, e);
		}
	}

}
