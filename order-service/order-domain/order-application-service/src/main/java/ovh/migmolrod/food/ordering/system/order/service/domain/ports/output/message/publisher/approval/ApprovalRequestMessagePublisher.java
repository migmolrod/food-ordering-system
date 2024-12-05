package ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.approval;

import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface ApprovalRequestMessagePublisher {

	void publish(
			OrderApprovalOutboxMessage orderApprovalOutboxMessage,
			BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback
	);

}
