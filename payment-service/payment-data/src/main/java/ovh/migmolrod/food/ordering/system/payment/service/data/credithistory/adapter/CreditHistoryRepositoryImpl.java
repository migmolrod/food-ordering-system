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
import java.util.stream.Collectors;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

	private final CreditHistoryJpaRepository jpaRepository;
	private final CreditHistoryDataAccessMapper mapper;

	public CreditHistoryRepositoryImpl(
			CreditHistoryJpaRepository jpaRepository,
			CreditHistoryDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public CreditHistory save(CreditHistory creditHistory) {
		CreditHistoryEntity savedCreditHistoryEntity = jpaRepository.save(
				mapper.creditHistoryToCreditHistoryEntity(creditHistory)
		);

		return mapper.creditHistoryEntityToCreditHistory(savedCreditHistoryEntity);
	}

	@Override
	public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
		Optional<List<CreditHistoryEntity>> creditHistoryEntities =
				jpaRepository.findByCustomerId(customerId.getValue());

		return creditHistoryEntities.map(
				creditHistoryList -> creditHistoryList.stream()
						.map(mapper::creditHistoryEntityToCreditHistory)
						.collect(Collectors.toList())
		);
	}

}
