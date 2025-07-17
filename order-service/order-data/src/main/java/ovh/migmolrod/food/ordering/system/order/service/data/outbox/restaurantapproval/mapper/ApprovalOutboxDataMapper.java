package ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;

@Component
public class ApprovalOutboxDataMapper {

	public ApprovalOutboxEntity messageToEntity(OrderApprovalOutboxMessage message) {
		return ApprovalOutboxEntity.builder()
				.id(message.getId())
				.sagaId(message.getSagaId())
				.createdAt(message.getCreatedAt())
				.type(message.getType())
				.payload(message.getPayload())
				.orderStatus(message.getOrderStatus())
				.sagaStatus(message.getSagaStatus())
				.outboxStatus(message.getOutboxStatus())
				.version(message.getVersion())
				.build();
	}

	public OrderApprovalOutboxMessage entityToMessage(ApprovalOutboxEntity entity) {
		return OrderApprovalOutboxMessage.builder()
				.id(entity.getId())
				.sagaId(entity.getSagaId())
				.createdAt(entity.getCreatedAt())
				.type(entity.getType())
				.payload(entity.getPayload())
				.orderStatus(entity.getOrderStatus())
				.sagaStatus(entity.getSagaStatus())
				.outboxStatus(entity.getOutboxStatus())
				.version(entity.getVersion())
				.build();
	}

}
