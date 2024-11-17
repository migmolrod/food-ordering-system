package ovh.migmolrod.food.ordering.system.order.service.domain.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.event.EmptyEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.saga.SagaStep;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

	private final OrderDomainService orderDomainService;
	private final OrderSagaHelper orderSagaHelper;
	private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

	public OrderApprovalSaga(
			OrderDomainService orderDomainService,
			OrderSagaHelper orderSagaHelper,
			OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher
	) {
		this.orderDomainService = orderDomainService;
		this.orderSagaHelper = orderSagaHelper;
		this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
	}

	@Override
	@Transactional
	public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
		log.info("Approving order with id {}", restaurantApprovalResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
		orderDomainService.approveOrder(order);
		orderSagaHelper.saveOrder(order);
		log.info("Order with id {} is approved", order.getId().getValue());

		return EmptyEvent.INSTANCE;
	}

	@Override
	@Transactional
	public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
		log.info("Cancelling order with id {}", restaurantApprovalResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
		OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(
				order,
				restaurantApprovalResponse.getFailureMessages(),
				orderCancelledPaymentRequestMessagePublisher
		);
		orderSagaHelper.saveOrder(order);
		log.info("Order with id {} is cancelling", order.getId().getValue());

		return orderCancelledEvent;
	}

}
