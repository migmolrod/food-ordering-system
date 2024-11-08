package ovh.migmolrod.food.ordering.system.kafka.producer.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Nonnull;

@Slf4j
@Component
public class KafkaMessageHelper {

	public <T> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(
			String requestTopicName,
			T avroModel,
			String orderId,
			String avroModelName
	) {
		return new ListenableFutureCallback<>() {
			@Override
			public void onFailure(@Nonnull Throwable exception) {
				log.error("""
								"Error while sending {} message {} to topic {}. Error: {}"
								""",
						avroModelName,
						avroModel.toString(),
						requestTopicName,
						exception.getMessage(),
						exception
				);
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
			}
		};
	}

}
