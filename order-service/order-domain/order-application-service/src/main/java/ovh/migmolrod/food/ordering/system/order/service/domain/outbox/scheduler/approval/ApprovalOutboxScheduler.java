package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.approval.ApprovalRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.outbox.OutboxScheduler;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ApprovalOutboxScheduler implements OutboxScheduler {

	private final ApprovalOutboxHelper approvalOutboxHelper;
	private final ApprovalRequestMessagePublisher approvalRequestMessagePublisher;

	public ApprovalOutboxScheduler(
			ApprovalOutboxHelper approvalOutboxHelper,
			ApprovalRequestMessagePublisher approvalRequestMessagePublisher
	) {
		this.approvalOutboxHelper = approvalOutboxHelper;
		this.approvalRequestMessagePublisher = approvalRequestMessagePublisher;
	}

	@Override
	@Transactional
	@Scheduled(
			fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
			initialDelayString = "${order-service.outbox-scheduler-initial-delay}"
	)
	public void processOutboxMessage() {
		Optional<List<OrderApprovalOutboxMessage>> approvalOutboxMessages =
				approvalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
						OutboxStatus.STARTED,
						SagaStatus.PROCESSING
				);

		if (approvalOutboxMessages.isPresent() && !approvalOutboxMessages.get().isEmpty()) {
			List<OrderApprovalOutboxMessage> outboxMessages = approvalOutboxMessages.get();
			log.info("Received {} OrderApprovalOutboxMessage with ids: {}, sending to message bus!",
					outboxMessages.size(),
					outboxMessages.stream()
							.map(outboxMessage ->
									outboxMessage.getId().toString())
							.collect(Collectors.joining(","))
			);
			outboxMessages.forEach(approvalOutboxMessage ->
					approvalRequestMessagePublisher.publish(approvalOutboxMessage, this::updateOutboxStatus)
			);
			log.info("{} OrderApprovalOutboxMessage(s) sent to message bus!", outboxMessages.size());
		}
	}

	public void updateOutboxStatus(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
		orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
		OrderApprovalOutboxMessage savedMessage = approvalOutboxHelper.save(orderApprovalOutboxMessage);
		log.info(
				"OrderApprovalOutboxMessage with saga id: {} is updated with outbox status: {}",
				savedMessage.getSagaId().toString(),
				outboxStatus.name()
		);
	}

}
