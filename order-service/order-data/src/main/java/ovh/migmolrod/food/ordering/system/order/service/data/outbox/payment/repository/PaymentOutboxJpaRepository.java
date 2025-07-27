package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.entity.PaymentOutboxEntity;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {

	Optional<List<PaymentOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(
			String type,
			OutboxStatus outboxStatus,
			Collection<SagaStatus> sagaStatus
	);

	Optional<PaymentOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(
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
