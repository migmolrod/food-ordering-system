package ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.entity.CreditHistoryEntity;
import ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.mapper.CreditHistoryDataAccessMapper;
import ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.repository.CreditHistoryJpaRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditHistory;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;

import java.util.List;
import java.util.Optional;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

	private final CreditHistoryJpaRepository creditHistoryJpaRepository;
	private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

	public CreditHistoryRepositoryImpl(
			CreditHistoryJpaRepository creditHistoryJpaRepository,
			CreditHistoryDataAccessMapper creditHistoryDataAccessMapper
	) {
		this.creditHistoryJpaRepository = creditHistoryJpaRepository;
		this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
	}

	@Override
	public CreditHistory save(CreditHistory creditHistory) {
		CreditHistoryEntity savedCreditHistoryEntity = creditHistoryJpaRepository.save(
				creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)
		);

		return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(savedCreditHistoryEntity);
	}

	@Override
	public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
		Optional<List<CreditHistoryEntity>> creditHistoryEntities =
				creditHistoryJpaRepository.findByCustomerId(customerId.getValue());

		return creditHistoryEntities.map(
				creditHistoryList -> creditHistoryList.stream()
						.map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
						.toList()
		);
	}

}
