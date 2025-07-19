package ovh.migmolrod.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class PaymentRequestMessagePublisherImpl implements PaymentRequestMessagePublisher {

	private final OrderMessagingDataMapper orderMessagingDataMapper;
	private final OrderServiceConfigData orderServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

	public PaymentRequestMessagePublisherImpl(
			OrderMessagingDataMapper orderMessagingDataMapper,
			OrderServiceConfigData orderServiceConfigData,
			KafkaMessageHelper kafkaMessageHelper,
			KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer
	) {
		this.orderMessagingDataMapper = orderMessagingDataMapper;
		this.orderServiceConfigData = orderServiceConfigData;
		this.kafkaMessageHelper = kafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(
			OrderPaymentOutboxMessage outboxMessage,
			BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback
	) {
		OrderPaymentEventPayload payload = this.kafkaMessageHelper.createOrderEventPayload(
				outboxMessage.getPayload(),
				OrderPaymentEventPayload.class
		);
		String sagaId = outboxMessage.getSagaId().toString();
		String orderId = payload.getOrderId();
		log.info("Received OrderPaymentOutboxMessage for order id '{}' and saga id '{}'", orderId, sagaId);

		try {
			PaymentRequestAvroModel avroModel = orderMessagingDataMapper.paymentEventPayloadToAvroModel(sagaId, payload);

			String topicName = this.orderServiceConfigData.getPaymentRequestTopicName();

			ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>> kafkaCallback =
					this.kafkaMessageHelper.getKafkaCallback(
							topicName,
							avroModel,
							outboxMessage,
							outboxCallback,
							orderId,
							"PaymentRequestAvroModel"
					);

			this.kafkaProducer.send(topicName, sagaId, avroModel, kafkaCallback);

			log.info("OrderPaymentEventPayload sent to Kafka for order id '{}' and saga id '{}'", orderId, sagaId);
		} catch (Exception e) {
			String errorMessage = String.format("""
					Could not send OrderPaymentEventPayload to Kafka for order id '%s' and saga id '%s'
					""", orderId, sagaId);
			log.error(errorMessage, e);
		}
	}

}
