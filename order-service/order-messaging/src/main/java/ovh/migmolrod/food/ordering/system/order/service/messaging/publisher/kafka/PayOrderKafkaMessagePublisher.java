package ovh.migmolrod.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

	private final OrderMessagingDataMapper orderMessagingDataMapper;
	private final OrderServiceConfigData orderServiceConfigData;
	private final OrderKafkaMessageHelper orderKafkaMessageHelper;
	private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;

	public PayOrderKafkaMessagePublisher(
			OrderMessagingDataMapper orderMessagingDataMapper,
			OrderServiceConfigData orderServiceConfigData,
			OrderKafkaMessageHelper orderKafkaMessageHelper,
			KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer
	) {
		this.orderMessagingDataMapper = orderMessagingDataMapper;
		this.orderServiceConfigData = orderServiceConfigData;
		this.orderKafkaMessageHelper = orderKafkaMessageHelper;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(OrderPaidEvent domainEvent) {
		String orderId = domainEvent.getOrder().getId().getValue().toString();
		log.info("Received OrderPaidEvent for order id: {}", orderId);

		try {
			RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
					orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

			kafkaProducer.send(
					orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
					orderId,
					restaurantApprovalRequestAvroModel,
					orderKafkaMessageHelper.getKafkaCallback(
							orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
							restaurantApprovalRequestAvroModel,
							orderId,
							"RestaurantApprovalRequestAvroModel"
					)
			);

			log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}", orderId);
		} catch (Exception e) {
			log.error("""
							Error while sending RestaurantApprovalRequestAvroModel message to kafka with order id: {}, error: {}",
							""",
					orderId,
					e.getMessage(),
					e
			);
		}
	}

}
