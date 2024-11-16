package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.event.EmptyEvent;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.saga.SagaStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

	public OrderPaymentSaga(
			OrderDomainService orderDomainService,
			OrderRepository orderRepository,
			OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher
	) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
	}

	@Override
	@Transactional
	public OrderPaidEvent process(PaymentResponse paymentResponse) {
		log.info("Completing payment for order with id {}", paymentResponse.getOrderId());
		Order order = findOrder(paymentResponse.getOrderId());
		OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
		orderRepository.save(order);
		log.info("Order with id {} has been paid", order.getId().getValue());

		return orderPaidEvent;
	}

	@Override
	@Transactional
	public EmptyEvent rollback(PaymentResponse paymentResponse) {
		log.info("Cancelling payment for order with id {}", paymentResponse.getOrderId());
		Order order = findOrder(paymentResponse.getOrderId());
		orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
		orderRepository.save(order);
		log.info("Order with id {} has been cancelled", order.getId().getValue());

		return EmptyEvent.INSTANCE;
	}

	private Order findOrder(String orderId) {
		Optional<Order> order = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
		if (order.isEmpty()) {
			String errorMessage = String.format("Order with id %s could not be found!", orderId);
			throw new OrderNotFoundException(errorMessage);
		}
		return order.get();
	}

}
