package ovh.migmolrod.food.ordering.system.order.service.domain.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
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
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

	private final OrderDomainService orderDomainService;
	private final OrderSagaHelper orderSagaHelper;
	private final PaymentOutboxHelper paymentOutboxHelper;
	private final ApprovalOutboxHelper approvalOutboxHelper;
	private final OrderDataMapper orderDataMapper;

	public OrderPaymentSaga(
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
	public void process(PaymentResponse paymentResponse) {
		Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
				paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(paymentResponse.getSagaId()),
						SagaStatus.STARTED
				);
		if (orderPaymentOutboxMessageResponse.isEmpty()) {
			log.info("An outbox message with saga id {} is already processed!", paymentResponse.getSagaId());
			return;
		}
		OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

		OrderPaidEvent orderPaidEvent = completePaymentForOrder(paymentResponse);

		SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(orderPaidEvent.getOrder().getStatus());
		paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
				orderPaymentOutboxMessage,
				orderPaidEvent.getOrder().getStatus(),
				sagaStatus
		));
		approvalOutboxHelper.saveApprovalOutboxMessage(
				orderDataMapper.orderPaidEventToOrderApprovalEventPayload(orderPaidEvent),
				orderPaidEvent.getOrder().getStatus(),
				sagaStatus,
				OutboxStatus.STARTED,
				UUID.fromString(paymentResponse.getSagaId())
		);

		log.info("Order with id {} has been paid", orderPaidEvent.getOrder().getId().getValue());
	}

	@Override
	@Transactional
	public void rollback(PaymentResponse paymentResponse) {
		Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
				paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(paymentResponse.getSagaId()),
						getCurrentSagaStatus(paymentResponse.getPaymentStatus())
				);
		if (orderPaymentOutboxMessageResponse.isEmpty()) {
			log.info("An outbox message with saga id {} is already rolled back!", paymentResponse.getSagaId());
			return;
		}
		OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

		Order order = rollbackPaymentForOrder(paymentResponse);

		SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getStatus());
		paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
				orderPaymentOutboxMessage,
				order.getStatus(),
				sagaStatus
		));

		if (PaymentStatus.CANCELLED.equals(paymentResponse.getPaymentStatus())) {
			approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
					paymentResponse.getSagaId(),
					order.getStatus(),
					sagaStatus
			));
		}

		log.info("Order with id {} has been cancelled", order.getId().getValue());
	}

	private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
			OrderPaymentOutboxMessage orderPaymentOutboxMessage,
			OrderStatus orderStatus,
			SagaStatus sagaStatus
	) {
		orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
		orderPaymentOutboxMessage.setOrderStatus(orderStatus);
		orderPaymentOutboxMessage.setSagaStatus(sagaStatus);

		return orderPaymentOutboxMessage;
	}

	private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
			String sagaId,
			OrderStatus orderStatus,
			SagaStatus sagaStatus
	) {
		Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
				approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
						UUID.fromString(sagaId),
						SagaStatus.COMPENSATING
				);
		if (orderApprovalOutboxMessageResponse.isEmpty()) {
			String errorMessage = String.format(
					"Approval outbox message could not be found in %s status for saga id %s",
					SagaStatus.COMPENSATING.name(),
					sagaId
			);
			log.error(errorMessage);
			throw new OrderDomainException(errorMessage);
		}
		OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
		orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
		orderApprovalOutboxMessage.setOrderStatus(orderStatus);
		orderApprovalOutboxMessage.setSagaStatus(sagaStatus);

		return orderApprovalOutboxMessage;
	}

	private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
		log.info("Completing payment for order with id {}", paymentResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
		OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
		orderSagaHelper.saveOrder(order);

		return orderPaidEvent;
	}

	private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
		log.info("Cancelling payment for order with id {}", paymentResponse.getOrderId());
		Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
		orderDomainService.cancelOrderPayment(order,
				paymentResponse.getFailureMessages());
		orderSagaHelper.saveOrder(order);

		return order;
	}

	private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
		return switch (paymentStatus) {
			case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
			case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
			case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
		};
	}

}
