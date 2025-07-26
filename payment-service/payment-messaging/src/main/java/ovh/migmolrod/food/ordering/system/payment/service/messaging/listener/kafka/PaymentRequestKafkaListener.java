package ovh.migmolrod.food.ordering.system.payment.service.messaging.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.consumer.KafkaConsumer;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import ovh.migmolrod.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

	private final PaymentRequestMessageListener paymentRequestMessageListener;
	private final PaymentMessagingDataMapper paymentMessagingDataMapper;

	public PaymentRequestKafkaListener(
			PaymentRequestMessageListener paymentRequestMessageListener,
			PaymentMessagingDataMapper paymentMessagingDataMapper
	) {
		this.paymentRequestMessageListener = paymentRequestMessageListener;
		this.paymentMessagingDataMapper = paymentMessagingDataMapper;
	}

	@Override
	@KafkaListener(
			id = "${kafka-consumer-config.payment-consumer-group-id}",
			topics = "${payment-service.payment-request-topic-name}"
	)
	public void receive(
			@Payload List<PaymentRequestAvroModel> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
			@Header(KafkaHeaders.OFFSET) List<Long> offsets
	) {
		log.info("""
						{} payment requests received with keys {}, partitions {} and offsets {}
						""",
				messages.size(),
				keys.toString(),
				partitions.toString(),
				offsets.toString()
		);

		messages.forEach(paymentRequestAvroModel -> {
			try {
				if (PaymentOrderStatus.PENDING.equals(paymentRequestAvroModel.getPaymentOrderStatus())) {
					log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());
					paymentRequestMessageListener.completePayment(
							paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel)
					);
				} else if (PaymentOrderStatus.CANCELLED.equals(paymentRequestAvroModel.getPaymentOrderStatus())) {
					log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
					paymentRequestMessageListener.cancelPayment(
							paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel)
					);
				}
			} catch (DataAccessException e) {
				SQLException sqlException = (SQLException) e.getRootCause();
				if (sqlException != null && sqlException.getSQLState() != null && sqlException.getErrorCode() == 1062) {
					/*
					 * NO-OP for unique key violation. This means another thread is trying to
					 * persist an outbox message with an already existing unique key.
					 */
					String preFormat = """
							Caught unique constraint violation in payment request with SQL state '%s' for order id '%s'
							""";
					String message = String.format(preFormat, sqlException.getSQLState(), paymentRequestAvroModel.getOrderId());
					log.error(message, e);
				} else {
					throw new PaymentApplicationServiceException("Throwing DataAccessException for payment request: " + e.getMessage(), e);
				}
			} catch (PaymentNotFoundException e) {
				/*
				 * NO-OP for payment not found.
				 * Do NOT throw an exception to prevent reading the data from Kafka again.
				 */
				String preFormat = "Caught payment not found exception in payment request listener for order id '%s'";
				String message = String.format(preFormat, paymentRequestAvroModel.getOrderId());
				log.error(message, e);
			}
		});
	}

}
