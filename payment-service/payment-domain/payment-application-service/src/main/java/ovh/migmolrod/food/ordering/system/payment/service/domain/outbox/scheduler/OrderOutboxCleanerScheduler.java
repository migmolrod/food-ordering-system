package ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class OrderOutboxCleanerScheduler implements OutboxScheduler {

	private final OrderOutboxHelper orderOutboxHelper;

	public OrderOutboxCleanerScheduler(OrderOutboxHelper orderOutboxHelper) {this.orderOutboxHelper = orderOutboxHelper;}

	@Override
	@Transactional
	@Scheduled(cron = "@midnight")
	public void processOutboxMessage() {
		Optional<List<OrderOutboxMessage>> orderOutboxMessages =
				this.orderOutboxHelper.getByOutboxStatus(OutboxStatus.COMPLETED);

		if (orderOutboxMessages.isPresent() && !orderOutboxMessages.get().isEmpty()) {
			List<OrderOutboxMessage> outboxMessages = orderOutboxMessages.get();
			log.info("Received {} OrderOutboxMessage(s) for clean-up. The payloads:\n{}",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(OrderOutboxMessage::getPayload)
							.collect(java.util.stream.Collectors.joining(",")));
			this.orderOutboxHelper.deleteByOutboxStatus(OutboxStatus.COMPLETED);
			log.info("{} OrderOutboxMessage(s) deleted!", outboxMessages.size());
		}
	}

}
