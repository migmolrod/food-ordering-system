package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {

	private final RestaurantApprovalRequestMessageListener listener;
	private final RestaurantMessagingDataMapper mapper;

	public RestaurantApprovalRequestKafkaListener(
			RestaurantApprovalRequestMessageListener listener,
			RestaurantMessagingDataMapper mapper
	) {
		this.listener = listener;
		this.mapper = mapper;
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
			try {
				log.info("Processing order approval for order id {} at {}",
						restaurantApprovalRequestAvroModel.getOrderId(), System.nanoTime());
				listener.approveOrder(
						mapper.restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(restaurantApprovalRequestAvroModel)
				);
			} catch (DataAccessException e) {
				SQLException sqlException = (SQLException) e.getRootCause();
				if (sqlException != null && sqlException.getSQLState() != null && sqlException.getErrorCode() == 1062) {
					/*
					 * NO-OP for unique key violation. This means another thread is trying to
					 * persist an outbox message with an already existing unique key.
					 */
					String preFormat = """
							Caught unique constraint violation in approval request with SQL state '%s' for order id '%s'
							""";
					String message = String.format(
							preFormat,
							sqlException.getSQLState(),
							restaurantApprovalRequestAvroModel.getOrderId()
					);
					log.error(message, e);
				} else {
					throw new RestaurantApplicationServiceException("Throwing DataAccessException for approval request: " + e.getMessage(), e);
				}
			} catch (RestaurantNotFoundException e) {
				// NO-OP when the restaurant for a given id can't found
				log.error(
						"Restaurant not found for order id '{}' and restaurant id '{}'",
						restaurantApprovalRequestAvroModel.getOrderId(),
						restaurantApprovalRequestAvroModel.getRestaurantId()
				);
			}
		});
	}

}
