package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentOutboxScheduler implements OutboxScheduler {

	private final PaymentOutboxHelper paymentOutboxHelper;
	private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

	public PaymentOutboxScheduler(
			PaymentOutboxHelper paymentOutboxHelper,
			PaymentRequestMessagePublisher paymentRequestMessagePublisher
	) {
		this.paymentOutboxHelper = paymentOutboxHelper;
		this.paymentRequestMessagePublisher = paymentRequestMessagePublisher;
	}

	@Override
	@Transactional
	@Scheduled(
			fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
			initialDelayString = "${order-service.outbox-scheduler-initial-delay}"
	)
	public void processOutboxMessage() {
		Optional<List<OrderPaymentOutboxMessage>> orderPaymentOutboxMessages =
				paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
						OutboxStatus.STARTED,
						SagaStatus.STARTED,
						SagaStatus.COMPENSATING
				);

		if (orderPaymentOutboxMessages.isPresent() && !orderPaymentOutboxMessages.get().isEmpty()) {
			List<OrderPaymentOutboxMessage> outboxMessages = orderPaymentOutboxMessages.get();
			log.info("Received {} OrderPaymentOutboxMessage with ids: {}, sending to message bus!",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(outboxMessage -> outboxMessage.getId().toString())
							.collect(Collectors.joining(","))
			);
			outboxMessages.forEach(orderPaymentOutboxMessage ->
					paymentRequestMessagePublisher.publish(orderPaymentOutboxMessage, this::updateOutboxStatus)
			);
			log.info("{} OrderPaymentOutboxMessage(s) sent to message bus!", outboxMessages.size());
		}
	}

	private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
		orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
		OrderPaymentOutboxMessage savedMessage = paymentOutboxHelper.save(orderPaymentOutboxMessage);
		log.info(
				"OrderPaymentOutboxMessage with saga id: {} is updated with outbox status: {}",
				savedMessage.getSagaId().toString(),
				outboxStatus.name()
		);
	}

}
