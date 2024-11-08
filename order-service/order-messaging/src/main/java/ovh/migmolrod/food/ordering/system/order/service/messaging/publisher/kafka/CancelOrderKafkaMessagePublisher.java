package ovh.migmolrod.food.ordering.system.order.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.helper.KafkaMessageHelper;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;
import ovh.migmolrod.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

	private final OrderMessagingDataMapper orderMessagingDataMapper;
	private final OrderServiceConfigData orderServiceConfigData;
	private final KafkaMessageHelper kafkaMessageHelper;
	private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

	public CancelOrderKafkaMessagePublisher(
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
	public void publish(OrderCancelledEvent domainEvent) {
		String orderId = domainEvent.getOrder().getId().getValue().toString();
		log.info("Received OrderCancelledEvent for order id: {}", orderId);

		try {
			PaymentRequestAvroModel paymentRequestAvroModel =
					orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(domainEvent);

			kafkaProducer.send(
					orderServiceConfigData.getPaymentRequestTopicName(),
					orderId,
					paymentRequestAvroModel,
					kafkaMessageHelper.getKafkaCallback(
							orderServiceConfigData.getPaymentRequestTopicName(),
							paymentRequestAvroModel,
							orderId,
							"PaymentRequestAvroModel"
					)
			);

			log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", orderId);
		} catch (Exception e) {
			log.error("""
							Error while sending PaymentRequestAvroModel message to Kafka with order id: {}. Error: {}
							""",
					orderId,
					e.getMessage(),
					e
			);
		}
	}

}
