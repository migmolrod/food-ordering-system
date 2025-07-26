package ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository;

import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {

	Optional<OrderOutboxMessage> findByTypeAndSagaIdAndApprovalStatusAndOutboxStatus(
			String orderSagaName,
			UUID sagaId,
			OrderApprovalStatus approvalStatus,
			OutboxStatus outboxStatus
	);

	Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(
			String orderSagaName,
			OutboxStatus outboxStatus
	);

	void deleteByTypeAndOutboxStatus(
			String orderSagaName,
			OutboxStatus outboxStatus
	);

	OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

}
