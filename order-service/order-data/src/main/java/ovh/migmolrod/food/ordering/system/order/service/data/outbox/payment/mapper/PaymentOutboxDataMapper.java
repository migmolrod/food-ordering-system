package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.entity.PaymentOutboxEntity;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;

@Component
public class PaymentOutboxDataMapper {

	public PaymentOutboxEntity messageToEntity(OrderPaymentOutboxMessage message) {
		return PaymentOutboxEntity.builder()
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

	public OrderPaymentOutboxMessage entityToMessage(PaymentOutboxEntity entity) {
		return OrderPaymentOutboxMessage.builder()
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
