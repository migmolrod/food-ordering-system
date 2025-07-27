package ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOutboxScheduler implements OutboxScheduler {

	private final OrderOutboxHelper orderOutboxHelper;
	private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

	public OrderOutboxScheduler(
			OrderOutboxHelper orderOutboxHelper,
			PaymentResponseMessagePublisher paymentResponseMessagePublisher
	) {
		this.orderOutboxHelper = orderOutboxHelper;
		this.paymentResponseMessagePublisher = paymentResponseMessagePublisher;
	}

	@Override
	@Transactional
	@Scheduled(
			fixedDelayString = "${payment-service.outbox-scheduler-fixed-rate}",
			initialDelayString = "${payment-service.outbox-scheduler-initial-delay}"
	)
	public void processOutboxMessage() {
		Optional<List<OrderOutboxMessage>> outboxMessagesResponse =
				this.orderOutboxHelper.getByOutboxStatus(OutboxStatus.STARTED);
		if (outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
			List<OrderOutboxMessage> outboxMessages = outboxMessagesResponse.get();
			log.info("Received {} OrderOutboxMessage(s) with ids {}, sending to message bus!",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(OrderOutboxMessage::getId)
							.map(Object::toString)
							.collect(Collectors.joining(",")));
			outboxMessages.forEach(orderOutboxMessage ->
					this.paymentResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxStatus));
			log.info("{} OrderOutboxMessage(s) to message bus!", outboxMessages.size());
		}
	}

}
