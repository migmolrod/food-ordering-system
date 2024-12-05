package ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository;

import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalOutboxRepository {
	
	OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage);

	Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	);

	Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
			String type,
			UUID sagaId,
			SagaStatus... sagaStatus
	);

	void deleteByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	);

}
