package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

	private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
	private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

	public RestaurantApprovalRequestKafkaListener(
			RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener,
			RestaurantMessagingDataMapper restaurantMessagingDataMapper
	) {
		this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
		this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
	}

	@Override
	@KafkaListener(
			id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
			topics = "${restaurant-service.restaurant-approval-request-topic-name}"
	)
	public void receive(
			@Payload List<RestaurantApprovalRequestAvroModel> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets
	) {
		log.info("""
				{} restaurant approval requests received with keys {}, partitions {} and offsets {}, sending order approval
				""", messages.size(), keys.toString(), partitions.toString(), offsets.toString());

		messages.forEach(restaurantApprovalRequestAvroModel -> {
			log.info("Processing order approval for order id {} at {}",
					restaurantApprovalRequestAvroModel.getOrderId(), System.nanoTime());
			restaurantApprovalRequestMessageListener.approveOrder(
					restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(restaurantApprovalRequestAvroModel)
			);
		});
	}

}