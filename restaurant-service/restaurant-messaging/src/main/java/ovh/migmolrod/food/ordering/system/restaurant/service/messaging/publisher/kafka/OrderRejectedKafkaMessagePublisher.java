package ovh.migmolrod.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;

@Slf4j
@Component
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMessagePublisher {

	private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
	private final RestaurantServiceConfigData restaurantServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;

	public OrderRejectedKafkaMessagePublisher(
			RestaurantMessagingDataMapper restaurantMessagingDataMapper,
			RestaurantServiceConfigData restaurantServiceConfigData,
			KafkaMessageHelper kafkaMessageHelper,
			KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer
	) {
		this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
		this.restaurantServiceConfigData = restaurantServiceConfigData;
		this.kafkaMessageHelper = kafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(OrderRejectedEvent domainEvent) {
		String orderId = domainEvent.getOrderApproval().getOrderId().getValue().toString();
		log.info("Received OrderRejectedEvent for order id {}", orderId);

		try {
			RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
					restaurantMessagingDataMapper.orderRejectedEventToRestaurantApprovalResponseAvroModel(domainEvent);

			kafkaProducer.send(
					restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
					orderId,
					restaurantApprovalResponseAvroModel,
					kafkaMessageHelper.getKafkaCallback(
							restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
							restaurantApprovalResponseAvroModel,
							orderId,
							"RestaurantApprovalResponseAvroModel"
					)
			);
		} catch (Exception e) {
			log.error("""
					Error while sending OrderRejectedEvent to kafka for order id: {}, error: {}
					""", orderId, e.getMessage());
		}
	}


}
