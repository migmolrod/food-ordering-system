package ovh.migmolrod.food.ordering.system.payment.service.data.payment.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

	@Id
	private UUID id;
	private UUID customerId;
	private UUID orderId;
	private BigDecimal price;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	private ZonedDateTime createdAt;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PaymentEntity that = (PaymentEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
