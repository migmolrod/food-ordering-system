package ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.entity.CreditEntryEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, UUID> {

	Optional<CreditEntryEntity> findByCustomerId(UUID customerId);

}
