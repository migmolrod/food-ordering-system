package ovh.migmolrod.food.ordering.system.payment.service.data.outbox.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.payment.service.data.outbox.entity.OrderOutboxEntity;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;

@Component
public class OrderOutboxDataMapper {

	public OrderOutboxEntity messageToEntity(OrderOutboxMessage message) {
		return OrderOutboxEntity.builder()
				.id(message.getId())
				.sagaId(message.getSagaId())
				.createdAt(message.getCreatedAt())
				.type(message.getType())
				.payload(message.getPayload())
				.outboxStatus(message.getOutboxStatus())
				.paymentStatus(message.getPaymentStatus())
				.version(message.getVersion())
				.build();
	}

	public OrderOutboxMessage entityToMessage(OrderOutboxEntity entity) {
		return OrderOutboxMessage.builder()
				.id(entity.getId())
				.sagaId(entity.getSagaId())
				.createdAt(entity.getCreatedAt())
				.type(entity.getType())
				.payload(entity.getPayload())
				.outboxStatus(entity.getOutboxStatus())
				.paymentStatus(entity.getPaymentStatus())
				.version(entity.getVersion())
				.build();
	}

}
