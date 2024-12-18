package ovh.migmolrod.food.ordering.system.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

	private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
	private final OrderMessagingDataMapper orderMessagingDataMapper;

	public RestaurantApprovalResponseKafkaListener(
			RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener,
			OrderMessagingDataMapper orderMessagingDataMapper
	) {
		this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
		this.orderMessagingDataMapper = orderMessagingDataMapper;
	}

	@Override
	@KafkaListener(
			id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
			topics = "${order-service.restaurant-approval-response-topic-name}"
	)
	public void receive(
			@Payload List<RestaurantApprovalResponseAvroModel> messages,
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

		messages.forEach(restaurantApprovalResponseAvroModel -> {
			if (OrderApprovalStatus.APPROVED.equals(restaurantApprovalResponseAvroModel.getOrderApprovalStatus())) {
				log.info("Processing approved order for order id: {}",
						restaurantApprovalResponseAvroModel.getOrderId());
				restaurantApprovalResponseMessageListener.orderApproved(
						orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
								restaurantApprovalResponseAvroModel
						)
				);
			} else if (OrderApprovalStatus.REJECTED.equals(restaurantApprovalResponseAvroModel.getOrderApprovalStatus())) {
				log.info("Processing rejected order for order id: {}",
						restaurantApprovalResponseAvroModel.getOrderId());
				restaurantApprovalResponseMessageListener.orderRejected(
						orderMessagingDataMapper.restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
								restaurantApprovalResponseAvroModel
						)
				);
			}
		});
	}

}
