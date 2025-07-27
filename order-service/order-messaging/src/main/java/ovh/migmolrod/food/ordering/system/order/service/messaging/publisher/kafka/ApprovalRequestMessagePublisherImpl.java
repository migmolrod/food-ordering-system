package ovh.migmolrod.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.approval.ApprovalRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class ApprovalRequestMessagePublisherImpl implements ApprovalRequestMessagePublisher {

	private final OrderMessagingDataMapper orderMessagingDataMapper;
	private final OrderServiceConfigData orderServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;

	public ApprovalRequestMessagePublisherImpl(
			OrderMessagingDataMapper orderMessagingDataMapper,
			OrderServiceConfigData orderServiceConfigData,
			KafkaMessageHelper kafkaMessageHelper,
			KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer
	) {
		this.orderMessagingDataMapper = orderMessagingDataMapper;
		this.orderServiceConfigData = orderServiceConfigData;
		this.kafkaMessageHelper = kafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(
			OrderApprovalOutboxMessage outboxMessage,
			BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback
	) {
		OrderApprovalEventPayload payload = this.kafkaMessageHelper.createOrderEventPayload(
				outboxMessage.getPayload(),
				OrderApprovalEventPayload.class
		);

		String sagaId = outboxMessage.getSagaId().toString();
		String orderId = payload.getOrderId();
		log.info("Received OrderApprovalOutboxMessage for order id '{}' and saga id '{}'", orderId, sagaId);

		try {
			RestaurantApprovalRequestAvroModel avroModel =
					orderMessagingDataMapper.approvalEventPayloadToAvroModel(sagaId, payload);

			String topicName = this.orderServiceConfigData.getRestaurantApprovalRequestTopicName();

			ListenableFutureCallback<SendResult<String, RestaurantApprovalRequestAvroModel>> kafkaCallback =
					this.kafkaMessageHelper.getKafkaCallback(
							topicName,
							avroModel,
							outboxMessage,
							outboxCallback,
							orderId,
							"RestaurantApprovalRequestAvroModel"
					);

			this.kafkaProducer.send(topicName, sagaId, avroModel, kafkaCallback);

			log.info("OrderApprovalEventPayload sent to Kafka for order id '{}' and saga id '{}'", orderId, sagaId);
		} catch (Exception e) {
			String errorMessage = String.format("""
					Could not send OrderApprovalEventPayload to Kafka for order id '%s' and saga id '%s'
					""", orderId, sagaId);
			log.error(errorMessage, e);
		}
	}

}
