package ovh.migmolrod.food.ordering.system.payment.service.data.credithistory.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.payment.service.domain.valueobject.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credit_histories")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditHistoryEntity {

	@Id
	private UUID id;
	private UUID customerId;
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CreditHistoryEntity that = (CreditHistoryEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
