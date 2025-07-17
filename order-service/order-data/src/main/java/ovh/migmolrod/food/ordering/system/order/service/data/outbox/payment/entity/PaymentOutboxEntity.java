package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paymennt_outbox")
@Entity
public class PaymentOutboxEntity {
	@Id
	private UUID id;
	private UUID sagaId;
	private ZonedDateTime createdAt;
	private ZonedDateTime processedAt;
	private String type;
	private String payload;
	@Enumerated(EnumType.STRING)
	private SagaStatus sagaStatus;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	@Enumerated(EnumType.STRING)
	private OutboxStatus outboxStatus;
	@Version
	private int version;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PaymentOutboxEntity that = (PaymentOutboxEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
