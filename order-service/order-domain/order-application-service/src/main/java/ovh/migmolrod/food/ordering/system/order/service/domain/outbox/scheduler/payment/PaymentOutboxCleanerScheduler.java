package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

	private final PaymentOutboxHelper paymentOutboxHelper;

	public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
		this.paymentOutboxHelper = paymentOutboxHelper;
	}

	@Override
	@Transactional
	@Scheduled(cron = "@midnight")
	public void processOutboxMessage() {
		Optional<List<OrderPaymentOutboxMessage>> orderPaymentOutboxMessages =
				paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
						OutboxStatus.COMPLETED,
						SagaStatus.SUCCEEDED,
						SagaStatus.FAILED,
						SagaStatus.COMPENSATED
				);

		if (orderPaymentOutboxMessages.isPresent() && !orderPaymentOutboxMessages.get().isEmpty()) {
			List<OrderPaymentOutboxMessage> outboxMessages = orderPaymentOutboxMessages.get();
			log.info("Received {} OrderPaymentOutboxMessage(s) for clean-up. The payloads:\n{}",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(OrderPaymentOutboxMessage::getPayload)
							.collect(Collectors.joining("\n"))
			);
			paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
					OutboxStatus.COMPLETED,
					SagaStatus.SUCCEEDED,
					SagaStatus.FAILED,
					SagaStatus.COMPENSATED
			);
			log.info("{} OrderPaymentOutboxMessage(s) deleted!", outboxMessages.size());
		}
	}

}
