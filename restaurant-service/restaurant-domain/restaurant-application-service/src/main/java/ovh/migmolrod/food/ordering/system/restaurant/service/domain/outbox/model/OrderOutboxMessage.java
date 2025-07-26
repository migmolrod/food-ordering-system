package ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {

	private UUID id;
	private UUID sagaId;
	private ZonedDateTime createdAt;
	private ZonedDateTime processedAt;
	private String type;
	private String payload;
	private OrderApprovalStatus approvalStatus;
	private OutboxStatus outboxStatus;
	private int version;

	public void setOutboxStatus(OutboxStatus outboxStatus) {
		this.outboxStatus = outboxStatus;
	}

}
