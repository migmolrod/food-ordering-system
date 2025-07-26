package ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_outbox")
@Entity
public class OrderOutboxEntity {

	@Id
	private UUID id;
	private UUID sagaId;
	private ZonedDateTime createdAt;
	private ZonedDateTime processedAt;
	private String type;
	private String payload;
	@Enumerated(EnumType.STRING)
	private OutboxStatus outboxStatus;
	@Enumerated(EnumType.STRING)
	private OrderApprovalStatus approvalStatus;
	@Version
	private int version;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderOutboxEntity that = (OrderOutboxEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
