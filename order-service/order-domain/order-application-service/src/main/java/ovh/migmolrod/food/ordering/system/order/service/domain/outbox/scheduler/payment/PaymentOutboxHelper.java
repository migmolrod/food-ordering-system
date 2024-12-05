package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ovh.migmolrod.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class PaymentOutboxHelper {

	private final PaymentOutboxRepository paymentOutboxRepository;
	private final ObjectMapper objectMapper;

	public PaymentOutboxHelper(
			PaymentOutboxRepository paymentOutboxRepository,
			ObjectMapper objectMapper
	) {
		this.paymentOutboxRepository = paymentOutboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public Optional<List<OrderPaymentOutboxMessage>> getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		return paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
				ORDER_SAGA_NAME,
				outboxStatus,
				sagaStatus
		);
	}

	@Transactional(readOnly = true)
	public Optional<OrderPaymentOutboxMessage> getPaymentOutboxMessageBySagaIdAndSagaStatus(
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
				ORDER_SAGA_NAME,
				sagaId,
				sagaStatus
		);
	}

	@Transactional
	public void deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
				ORDER_SAGA_NAME,
				outboxStatus,
				sagaStatus
		);
	}

	@Transactional
	public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
		OrderPaymentOutboxMessage response = paymentOutboxRepository.save(orderPaymentOutboxMessage);
		if (response == null) {
			String errorMessage = String.format(
					"Could not save OrderPaymentOutboxMessage with outbox id: %s",
					orderPaymentOutboxMessage.getId().toString()
			);
			log.error(errorMessage);
			throw new OrderDomainException(errorMessage);
		}

		log.info("OrderPaymentOutboxMessage saved with id: {}", response.getId());
		return response;
	}

	@Transactional
	public void savePaymentOutboxMessage(
			OrderPaymentEventPayload orderPaymentEventPayload,
			OrderStatus orderStatus,
			SagaStatus sagaStatus,
			OutboxStatus outboxStatus,
			UUID sagaId
	) {
		this.save(OrderPaymentOutboxMessage.builder()
				.id(UUID.randomUUID())
				.sagaId(sagaId)
				.createdAt(orderPaymentEventPayload.getCreatedAt())
				.type(ORDER_SAGA_NAME)
				.payload(buildPayload(orderPaymentEventPayload))
				.sagaStatus(sagaStatus)
				.outboxStatus(outboxStatus)
				.orderStatus(orderStatus)
				.build());
	}

	private String buildPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
		try {
			return objectMapper.writeValueAsString(orderPaymentEventPayload);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format(
					"Could not map object OrderPaymentEventPayload to JSON for order with id: %s",
					orderPaymentEventPayload.getOrderId()
			);
			log.error(errorMessage, e);
			throw new OrderDomainException(errorMessage, e);
		}
	}

}
