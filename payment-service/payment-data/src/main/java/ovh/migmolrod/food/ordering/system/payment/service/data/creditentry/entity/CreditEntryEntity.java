package ovh.migmolrod.food.ordering.system.payment.service.data.creditentry.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credit_entries")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditEntryEntity {

	@Id
	private UUID id;
	private UUID customerId;
	private BigDecimal totalCreditAmount;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CreditEntryEntity that = (CreditEntryEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
