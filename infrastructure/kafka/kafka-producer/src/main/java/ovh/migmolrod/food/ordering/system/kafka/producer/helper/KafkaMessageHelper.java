package ovh.migmolrod.food.ordering.system.kafka.producer.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

	private final ObjectMapper objectMapper;

	public KafkaMessageHelper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public <T, U> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(
			String requestTopicName,
			T avroModel,
			U outboxMessage,
			BiConsumer<U, OutboxStatus> outboxCallback,
			String orderId,
			String avroModelName
	) {
		return new ListenableFutureCallback<>() {
			@Override
			public void onFailure(@Nonnull Throwable exception) {
				log.error("""
								"Error while sending '{}' message '{}' and outbox type '{}' to topic '{}'"
								""",
						avroModelName,
						avroModel.toString(),
						outboxMessage.getClass().getName(),
						requestTopicName,
						exception
				);
				outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
			}

			@Override
			public void onSuccess(SendResult<String, T> result) {
				RecordMetadata recordMetadata = result.getRecordMetadata();
				log.info("""
								Received successful response from Kafka for order id: {}, Topic: {}, Partition: {}, Offset: {}, Timestamp: {}
								""",
						orderId,
						recordMetadata.topic(),
						recordMetadata.partition(),
						recordMetadata.offset(),
						recordMetadata.timestamp()
				);
				outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
			}
		};
	}

	public <T> T createOrderEventPayload(String payload, Class<T> outputType){
		try {
			return this.objectMapper.readValue(payload, outputType);
		} catch (JsonProcessingException e) {
			String message = String.format("Could not read '%s' from payload: %s", outputType.getName(), payload);
			log.error(message, e);
			throw new OrderDomainException(message);
		}
	}

}
