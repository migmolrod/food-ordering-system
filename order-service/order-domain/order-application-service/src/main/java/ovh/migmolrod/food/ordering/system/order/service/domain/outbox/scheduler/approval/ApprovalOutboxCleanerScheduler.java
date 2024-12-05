package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApprovalOutboxCleanerScheduler implements OutboxScheduler {

	private final ApprovalOutboxHelper approvalOutboxHelper;

	public ApprovalOutboxCleanerScheduler(ApprovalOutboxHelper approvalOutboxHelper) {
		this.approvalOutboxHelper = approvalOutboxHelper;
	}

	@Override
	@Transactional
	@Scheduled(cron = "@midnight")
	public void processOutboxMessage() {
		Optional<List<OrderApprovalOutboxMessage>> orderApprovalOutboxMessages =
				approvalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
						OutboxStatus.COMPLETED,
						SagaStatus.SUCCEEDED,
						SagaStatus.FAILED,
						SagaStatus.COMPENSATED
				);

		if (orderApprovalOutboxMessages.isPresent() && !orderApprovalOutboxMessages.get().isEmpty()) {
			List<OrderApprovalOutboxMessage> outboxMessages = orderApprovalOutboxMessages.get();
			log.info("Received {} OrderApprovalOutboxMessage(s) for clean-up. The payloads:\n{}",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(OrderApprovalOutboxMessage::getPayload)
							.collect(Collectors.joining("\n"))
			);
			approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
					OutboxStatus.COMPLETED,
					SagaStatus.SUCCEEDED,
					SagaStatus.FAILED,
					SagaStatus.COMPENSATED
			);
			log.info("{} OrderApprovalOutboxMessage(s) deleted!", outboxMessages.size());
		}
	}

}
