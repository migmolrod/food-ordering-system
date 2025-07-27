package ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalOutboxJpaRepository extends JpaRepository<ApprovalOutboxEntity, UUID> {

	Optional<List<ApprovalOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(
			String type,
			OutboxStatus outboxStatus,
			Collection<SagaStatus> sagaStatus
	);

	Optional<ApprovalOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(
			String type,
			UUID sagaId,
			Collection<SagaStatus> sagaStatus
	);

	void deleteByTypeAndOutboxStatusAndSagaStatusIn(
			String type,
			OutboxStatus outboxStatus,
			Collection<SagaStatus> sagaStatus
	);

}
