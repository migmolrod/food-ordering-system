package ovh.migmolrod.food.ordering.system.customer.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.output.messaging.publisher.CustomerMessagePublisher;
import ovh.migmolrod.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingMapper;
import ovh.migmolrod.food.ordering.system.kafka.customer.avro.model.CustomerAvroModel;
import ovh.migmolrod.food.ordering.system.kafka.producer.service.KafkaProducer;

@Slf4j
@Component
public class CustomerCreatedEventKafkaPublisher implements CustomerMessagePublisher {

	private final CustomerMessagingMapper mapper;
	private final CustomerServiceConfigData configData;
	private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;

	public CustomerCreatedEventKafkaPublisher(
			CustomerMessagingMapper mapper,
			CustomerServiceConfigData configData,
			KafkaProducer<String, CustomerAvroModel> kafkaProducer
	) {
		this.mapper = mapper;
		this.configData = configData;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publish(CustomerCreatedEvent event) {
		log.info("Received CustomerCreatedEvent for customer id '{}'", event.getCustomer().getId().getValue());

		try {
			CustomerAvroModel avroModel = this.mapper.eventToAvroModel(event);
			String topicName = this.configData.getCustomerTopicName();
			this.kafkaProducer.send(
					topicName,
					avroModel.getId(),
					avroModel,
					this.getCallback(topicName, avroModel)
			);
			log.info("CustomerCreatedEvent sent to Kafka for customer id '{}'", event.getCustomer().getId().getValue());
		} catch (Exception e) {
			String errorMessage = String.format("Could not send CustomerCreatedEvent to Kafka for customer id '%s'",
					event.getCustomer().getId().getValue());
			log.error(errorMessage, e);
		}

	}

	private ListenableFutureCallback<SendResult<String, CustomerAvroModel>> getCallback(
			String topicName,
			CustomerAvroModel message
	) {
		return new ListenableFutureCallback<>() {
			@Override
			public void onFailure(Throwable ex) {
				String errorMessage = String.format("""
								Error while sending CustomerCreatedEvent to Kafka for customer id '%s' and Kafka topic '%s'.
								""",
						message.getId(), topicName);
				log.error(errorMessage, ex);
			}

			@Override
			public void onSuccess(SendResult<String, CustomerAvroModel> result) {
				RecordMetadata metadata = result.getRecordMetadata();
				log.info("CustomerCreatedEvent metadata. Topic '{}'; Partition '{}'; Offset '{}'; Timestamp '{}'; at '{}.",
						metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp(), System.nanoTime());
			}
		};
	}

}
