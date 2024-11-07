package ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.entity.CreditHistoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistoryEntity, UUID> {

	Optional<List<CreditHistoryEntity>> findByCustomerId(UUID customerId);

}
