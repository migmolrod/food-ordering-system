package ovh.migmolrod.food.ordering.system.order.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import ovh.migmolrod.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

	public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
		Order order = orderCreatedEvent.getOrder();

		return PaymentRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setCustomerId(order.getCustomerId().getValue().toString())
				.setOrderId(order.getId().getValue().toString())
				.setPrice(order.getPrice().getAmount())
				.setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
				.setPaymentOrderStatus(PaymentOrderStatus.PENDING)
				.build();
	}


	public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
		Order order = orderCancelledEvent.getOrder();

		return PaymentRequestAvroModel.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setSagaId("")
				.setCustomerId(order.getCustomerId().getValue().toString())
				.setOrderId(order.getId().getValue().toString())
				.setPrice(order.getPrice().getAmount())
				.setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
				.setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
				.build();
	}
}
