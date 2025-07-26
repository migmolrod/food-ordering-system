package ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.ApprovalResponseMessagePublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOutboxScheduler implements OutboxScheduler {

	private final OrderOutboxHelper orderOutboxHelper;
	private final ApprovalResponseMessagePublisher approvalResponseMessagePublisher;

	public OrderOutboxScheduler(
			OrderOutboxHelper orderOutboxHelper,
			ApprovalResponseMessagePublisher approvalResponseMessagePublisher
	) {
		this.orderOutboxHelper = orderOutboxHelper;
		this.approvalResponseMessagePublisher = approvalResponseMessagePublisher;
	}

	@Override
	@Transactional
	@Scheduled(
			fixedDelayString = "${restaurant-service.outbox-scheduler-fixed-rate}",
			initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}"
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
					this.approvalResponseMessagePublisher.publish(orderOutboxMessage, orderOutboxHelper::updateOutboxStatus));
			log.info("{} OrderOutboxMessage(s) to message bus!", outboxMessages.size());
		}
	}

}
