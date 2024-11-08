package ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.entity.CreditHistoryEntity;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditHistory;
import ovh.migmolrod.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;

@Component
public class CreditHistoryDataAccessMapper {

	public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
		return CreditHistoryEntity.builder()
				.id(creditHistory.getId().getValue())
				.customerId(creditHistory.getCustomerId().getValue())
				.amount(creditHistory.getAmount().getAmount())
				.transactionType(creditHistory.getTransactionType())
				.build();
	}

	public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
		return CreditHistory.builder()
				.creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
				.customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
				.amount(new Money(creditHistoryEntity.getAmount()))
				.transactionType(creditHistoryEntity.getTransactionType())
				.build();
	}

}
