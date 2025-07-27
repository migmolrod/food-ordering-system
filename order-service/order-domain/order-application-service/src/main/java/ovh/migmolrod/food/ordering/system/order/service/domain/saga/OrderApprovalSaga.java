package ovh.migmolrod.food.ordering.system.order.service.domain.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStep;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

	private final OrderDomainService orderDomainService;
	private final OrderSagaHelper orderSagaHelper;
	private final PaymentOutboxHelper paymentOutboxHelper;
	private final ApprovalOutboxHelper approvalOutboxHelper;
	private final OrderDataMapper orderDataMapper;

	public OrderApprovalSaga(
			OrderDomainService orderDomainService,
			OrderSagaHelper orderSagaHelper,
			PaymentOutboxHelper paymentOutboxHelper,
			ApprovalOutboxHelper approvalOutboxHelper,
			OrderDataMapper orderDataMapper
	) {
		this.orderDomainService = orderDomainService;
		this.orderSagaHelper = orderSagaHelper;
		this.paymentOutboxHelper = paymentOutboxHelper;
		this.approvalOutboxHelper = approvalOutboxHelper;
		this.orderDataMapper = orderDataMapper;
	}

	@Override
	@Transactional
	public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
		Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
				approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(restaurantApprovalResponse.getSagaId()),
						SagaStatus.PROCESSING
				);
		if (orderApprovalOutboxMessageResponse.isEmpty()) {
			log.info("An outbox message with saga id {} is already processed!", restaurantApprovalResponse.getSagaId());
			return;
		}
		OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();

		Order order = approveOrder(restaurantApprovalResponse);
		SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getStatus());

		approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
				orderApprovalOutboxMessage,
				order.getStatus(),
				sagaStatus
		));

		paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
				restaurantApprovalResponse.getSagaId(),
				order.getStatus(),
				sagaStatus
		));

		log.info("Order with id {} is approved", order.getId().getValue());
	}

	@Override
	@Transactional
	public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
		Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
				approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(restaurantApprovalResponse.getSagaId()),
						SagaStatus.PROCESSING
				);
		if (orderApprovalOutboxMessageResponse.isEmpty()) {
			log.info("An outbox message with saga id {} is already rolled back!", restaurantApprovalResponse.getSagaId());
			return;
		}
		OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();

		OrderCancelledEvent orderCancelledEvent = rollbackOrder(restaurantApprovalResponse);

		SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderCancelledEvent.getOrder().getStatus());

		approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
				orderApprovalOutboxMessage,
				orderCancelledEvent.getOrder().getStatus(),
				sagaStatus
		));

		paymentOutboxHelper.savePaymentOutboxMessage(
				orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(orderCancelledEvent),
				orderCancelledEvent.getOrder().getStatus(),
				sagaStatus,
				OutboxStatus.STARTED,
				UUID.fromString(restaurantApprovalResponse.getSagaId())
		);

		log.info("Order with id {} is cancelling", orderCancelledEvent.getOrder().getId().getValue());
	}

	private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
			OrderApprovalOutboxMessage orderApprovalOutboxMessage,
			OrderStatus orderStatus,
			SagaStatus sagaStatus
	) {
		orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
		orderApprovalOutboxMessage.setOrderStatus(orderStatus);
		orderApprovalOutboxMessage.setSagaStatus(sagaStatus);

		return orderApprovalOutboxMessage;
	}

	private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
			String sagaId,
			OrderStatus orderStatus,
			SagaStatus sagaStatus
	) {
		Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
				paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(sagaId),
						SagaStatus.PROCESSING
				);
		if (orderPaymentOutboxMessageResponse.isEmpty()) {
			String errorMessage = String.format(
					"Payment outbox message could not be found in %s status for saga id %s",
					SagaStatus.PROCESSING.name(),
					sagaId
			);
			log.error(errorMessage);
			throw new OrderDomainException(errorMessage);
		}
		OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
		orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
		orderPaymentOutboxMessage.setOrderStatus(orderStatus);
		orderPaymentOutboxMessage.setSagaStatus(sagaStatus);

		return orderPaymentOutboxMessage;
	}

	private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
		log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
		orderDomainService.approveOrder(order);
		orderSagaHelper.saveOrder(order);
		return order;
	}

	private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
		log.info("Cancelling order with id {}", restaurantApprovalResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
		OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(
				order,
				restaurantApprovalResponse.getFailureMessages()
		);
		orderSagaHelper.saveOrder(order);
		return orderCancelledEvent;
	}

}
