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

	private final CreditEntryJpaRepository jpaRepository;
	private final CreditEntryDataAccessMapper mapper;


	public CreditEntryRepositoryImpl(
			CreditEntryJpaRepository jpaRepository,
			CreditEntryDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public CreditEntry save(CreditEntry creditEntry) {
		CreditEntryEntity savedCreditEntryEntity = jpaRepository.save(
				mapper.creditEntryToCreditEntryEntity(creditEntry)
		);

		return mapper.creditEntryEntityToCreditEntry(savedCreditEntryEntity);
	}

	@Override
	public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
		return jpaRepository
				.findByCustomerId(customerId.getValue())
				.map(mapper::creditEntryEntityToCreditEntry);
	}

}
