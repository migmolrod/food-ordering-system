package ovh.migmolrod.food.ordering.system.payment.service.data.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.data.outbox.entity.OrderOutboxEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {

	Optional<List<OrderOutboxEntity>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

	Optional<OrderOutboxEntity> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
			String type,
			UUID sagaId,
			PaymentStatus paymentStatus,
			OutboxStatus outboxStatus
	);

	void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

}
