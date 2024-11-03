package ovh.migmolrod.food.ordering.system.payment.service.domain.entity;

import ovh.migmolrod.food.ordering.system.domain.entity.BaseEntity;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;

public class CreditEntry extends BaseEntity<CreditEntryId> {

	private final CustomerId customerId;
	private Money totalCreditAccount;

	private CreditEntry(Builder builder) {
		setId(builder.creditEntryId);
		customerId = builder.customerId;
		totalCreditAccount = builder.totalCreditAccount;
	}

	public static Builder builder() {
		return new Builder();
	}

	public void addCreditAmmount(Money amount) {
		totalCreditAccount = totalCreditAccount.add(amount);
	}

	public void subtractCreditAmount(Money amount) {
		totalCreditAccount = totalCreditAccount.subtract(amount);
	}

	public CustomerId getCustomerId() {
		return customerId;
	}

	public Money getTotalCreditAccount() {
		return totalCreditAccount;
	}

	public static final class Builder {

		private CreditEntryId creditEntryId;
		private CustomerId customerId;
		private Money totalCreditAccount;

		private Builder() {}

		public static Builder builder() {
			return new Builder();
		}

		public Builder creditEntryId(CreditEntryId val) {
			creditEntryId = val;
			return this;
		}

		public Builder customerId(CustomerId val) {
			customerId = val;
			return this;
		}

		public Builder totalCreditAccount(Money val) {
			totalCreditAccount = val;
			return this;
		}

		public CreditEntry build() {
			return new CreditEntry(this);
		}

	}

}
