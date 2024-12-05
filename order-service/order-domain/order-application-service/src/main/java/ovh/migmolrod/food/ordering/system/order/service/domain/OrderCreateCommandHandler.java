package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import ovh.migmolrod.food.ordering.system.order.service.domain.saga.OrderSagaHelper;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {

	private final OrderCreateHelper orderCreateHelper;
	private final OrderDataMapper orderDataMapper;
	private final PaymentOutboxHelper paymentOutboxHelper;
	private final OrderSagaHelper orderSagaHelper;

	public OrderCreateCommandHandler(
			OrderCreateHelper orderCreateHelper,
			OrderDataMapper orderDataMapper,
			PaymentOutboxHelper paymentOutboxHelper,
			OrderSagaHelper orderSagaHelper
	) {
		this.orderCreateHelper = orderCreateHelper;
		this.orderDataMapper = orderDataMapper;
		this.paymentOutboxHelper = paymentOutboxHelper;
		this.orderSagaHelper = orderSagaHelper;
	}

	@Transactional
	public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
		OrderCreatedEvent orderCreatedEvent = this.orderCreateHelper.persistOrder(createOrderCommand);
		log.info("Order has been created with id {}", orderCreatedEvent.getOrder().getId().getValue());

		CreateOrderResponse response = orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order " +
				"created " +
				"successfully");

		paymentOutboxHelper.savePaymentOutboxMessage(
				orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
				orderCreatedEvent.getOrder().getStatus(),
				orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getStatus()),
				OutboxStatus.STARTED,
				UUID.randomUUID()
		);

		return response;
	}

}
