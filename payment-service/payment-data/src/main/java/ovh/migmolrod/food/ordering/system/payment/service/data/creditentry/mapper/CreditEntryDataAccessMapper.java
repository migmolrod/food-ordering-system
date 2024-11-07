package ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.entity.CreditEntryEntity;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;
import ovh.migmolrod.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;

@Component
public class CreditEntryDataAccessMapper {

	public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
		return CreditEntryEntity.builder()
				.id(creditEntry.getId().getValue())
				.customerId(creditEntry.getCustomerId().getValue())
				.totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
				.build();
	}

	public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
		return CreditEntry.builder()
				.creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
				.customerId(new CustomerId(creditEntryEntity.getCustomerId()))
				.totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
				.build();
	}

}
