package ovh.migmolrod.food.ordering.system.payment.service.data.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.payment.service.data.payment.entity.PaymentEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

	Optional<PaymentEntity> findByOrderId(UUID orderId);

}
