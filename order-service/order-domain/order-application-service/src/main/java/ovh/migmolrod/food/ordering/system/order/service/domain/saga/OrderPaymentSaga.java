package ovh.migmolrod.food.ordering.system.order.service.domain.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.event.EmptyEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.saga.SagaStep;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

	private final OrderDomainService orderDomainService;
	private final OrderSagaHelper orderSagaHelper;
	private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

	public OrderPaymentSaga(
			OrderDomainService orderDomainService,
			OrderSagaHelper orderSagaHelper,
			OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher
	) {
		this.orderDomainService = orderDomainService;
		this.orderSagaHelper = orderSagaHelper;
		this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
	}

	@Override
	@Transactional
	public OrderPaidEvent process(PaymentResponse paymentResponse) {
		log.info("Completing payment for order with id {}", paymentResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
		OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
		orderSagaHelper.saveOrder(order);
		log.info("Order with id {} has been paid", order.getId().getValue());

		return orderPaidEvent;
	}

	@Override
	@Transactional
	public EmptyEvent rollback(PaymentResponse paymentResponse) {
		log.info("Cancelling payment for order with id {}", paymentResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
		orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
		orderSagaHelper.saveOrder(order);
		log.info("Order with id {} has been cancelled", order.getId().getValue());

		return EmptyEvent.INSTANCE;
	}

}
