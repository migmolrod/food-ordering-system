package ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.entity.CreditEntryEntity;
import ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.mapper.CreditEntryDataAccessMapper;
import ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.repository.CreditEntryJpaRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

	private final CreditEntryJpaRepository creditEntryJpaRepository;
	private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;


	public CreditEntryRepositoryImpl(
			CreditEntryJpaRepository creditEntryJpaRepository,
			CreditEntryDataAccessMapper creditEntryDataAccessMapper
	) {
		this.creditEntryJpaRepository = creditEntryJpaRepository;
		this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
	}

	@Override
	public CreditEntry save(CreditEntry creditEntry) {
		CreditEntryEntity savedCreditEntryEntity = creditEntryJpaRepository.save(
				creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)
		);

		return creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(savedCreditEntryEntity);
	}

	@Override
	public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
		return creditEntryJpaRepository
				.findByCustomerId(customerId.getValue())
				.map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
	}

}
