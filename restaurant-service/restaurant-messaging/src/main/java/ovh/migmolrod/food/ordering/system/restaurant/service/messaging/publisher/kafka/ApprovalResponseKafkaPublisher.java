package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.ApprovalResponseMessagePublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class ApprovalResponseKafkaPublisher implements ApprovalResponseMessagePublisher {

	private final RestaurantMessagingDataMapper dataMapper;
	private final RestaurantServiceConfigData configData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;

	public ApprovalResponseKafkaPublisher(RestaurantMessagingDataMapper dataMapper,
	                                      RestaurantServiceConfigData configData, KafkaMessageHelper kafkaMessageHelper,
	                                      KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer) {
		this.dataMapper = dataMapper;
		this.configData = configData;
		this.kafkaMessageHelper = kafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(
			OrderOutboxMessage outboxMessage,
			BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback
	) {
		OrderEventPayload payload = this.kafkaMessageHelper.createOrderEventPayload(
				outboxMessage.getPayload(),
				OrderEventPayload.class
		);
		String sagaId = outboxMessage.getSagaId().toString();
		String orderId = payload.getOrderId();
		log.info("Received OrderOutboxMessage for order id '{}' and saga id '{}'", orderId, sagaId);

		try {
			RestaurantApprovalResponseAvroModel avroModel =
					this.dataMapper.orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, payload);

			String topicName = this.configData.getRestaurantApprovalResponseTopicName();

			ListenableFutureCallback<SendResult<String, RestaurantApprovalResponseAvroModel>> kafkaCallback =
					this.kafkaMessageHelper.getKafkaCallback(
					topicName,
					avroModel,
					outboxMessage,
					outboxCallback,
					orderId,
					"RestaurantApprovalResponseAvroModel"
			);

			this.kafkaProducer.send(topicName, sagaId, avroModel, kafkaCallback);

			log.info("RestaurantApprovalResponseAvroModel sent to kafka for order id '{}' and saga id '{}'",
					orderId, sagaId);
		} catch (Exception e) {
			String errorMessage = String.format("""
					Could not send RestaurantApprovalResponseAvroModel to Kafka for order id '%s' and saga id '%s'
					""", orderId, sagaId);
			log.error(errorMessage, e);
		}
	}

}
