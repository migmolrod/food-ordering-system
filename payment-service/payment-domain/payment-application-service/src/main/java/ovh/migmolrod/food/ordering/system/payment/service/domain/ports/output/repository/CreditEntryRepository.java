package ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository;

import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

	CreditEntry save(CreditEntry creditEntry);

	Optional<CreditEntry> findByCustomerId(CustomerId customerId);

}
