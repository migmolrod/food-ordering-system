package ovh.migmolrod.food.ordering.system.order.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.customer.avro.model.CustomerAvroModel;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.customer.CustomerCreatedMessageListener;
import ovh.migmolrod.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;

import java.util.List;

@Slf4j
@Component
public class CustomerKafkaListener implements KafkaConsumer<CustomerAvroModel> {

	private final CustomerCreatedMessageListener listener;
	private final OrderMessagingDataMapper mapper;

	public CustomerKafkaListener(CustomerCreatedMessageListener listener, OrderMessagingDataMapper mapper) {
		this.listener = listener;
		this.mapper = mapper;
	}

	@Override
	@KafkaListener(
			id = "${kafka-consumer-config.customer-consumer-group-id}",
			topics = "${order-service.customer-topic-name}"
	)
	public void receive(
			@Payload List<CustomerAvroModel> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets
	) {
		log.info("""
				{} customer created messages received with keys {}, partitions {} and offsets {}
				""", messages.size(), keys.toString(), partitions.toString(), offsets.toString());

		messages.forEach(customerAvroModel -> {
			log.info("Processing CustomerCreated message for customer with username '{}'", customerAvroModel.getUsername());
			this.listener.customerCreated(this.mapper.customerAvroModelToCustomerCreatedModel(customerAvroModel));
		});
	}

}
