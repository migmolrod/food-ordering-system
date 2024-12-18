package ovh.migmolrod.food.ordering.system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {

	public static final Money ZERO = new Money(BigDecimal.ZERO);
	private final BigDecimal amount;

	public Money(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public boolean isGreaterThanZero() {
		return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean isGreaterThan(Money money) {
		return this.amount != null && this.amount.compareTo(money.getAmount()) > 0;
	}

	public Money add(Money money) {
		return new Money(this.setScale(this.amount.add(money.getAmount())));
	}

	public Money subtract(Money money) {
		return new Money(this.setScale(this.amount.subtract(money.getAmount())));
	}

	public Money multiply(int multiplier) {
		return new Money(this.setScale(this.amount.multiply(new BigDecimal(multiplier))));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Money money = (Money) o;
		return getAmount().equals(money.getAmount());
	}

	@Override
	public int hashCode() {
		return getAmount().hashCode();
	}

	private BigDecimal setScale(BigDecimal input) {
		return input.setScale(2, RoundingMode.HALF_EVEN);
	}

}
