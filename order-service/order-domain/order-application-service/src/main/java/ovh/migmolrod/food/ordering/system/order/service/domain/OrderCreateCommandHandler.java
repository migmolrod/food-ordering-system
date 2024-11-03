package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;

@Slf4j
@Component
public class OrderCreateCommandHandler {

	private final OrderCreateHelper orderCreateHelper;
	private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;
	private final OrderDataMapper orderDataMapper;

	public OrderCreateCommandHandler(
			OrderCreateHelper orderCreateHelper,
			OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher,
			OrderDataMapper orderDataMapper
	) {
		this.orderCreateHelper = orderCreateHelper;
		this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
		this.orderDataMapper = orderDataMapper;
	}

	public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
		OrderCreatedEvent orderCreatedEvent = this.orderCreateHelper.persistOrder(createOrderCommand);
		log.info("Order has been created with id {}", orderCreatedEvent.getOrder().getId().getValue());

		orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);

		return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order created successfully");
	}

}
