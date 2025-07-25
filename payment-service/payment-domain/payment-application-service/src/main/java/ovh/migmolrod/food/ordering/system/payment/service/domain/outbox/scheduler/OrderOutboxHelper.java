package ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentDomainException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;
import static ovh.migmolrod.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class OrderOutboxHelper {

	private final OrderOutboxRepository orderOutboxRepository;
	private final ObjectMapper objectMapper;

	public OrderOutboxHelper(
			OrderOutboxRepository orderOutboxRepository,
			ObjectMapper objectMapper
	) {
		this.orderOutboxRepository = orderOutboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
		OrderOutboxMessage response = this.orderOutboxRepository.save(orderOutboxMessage);
		if (response == null) {
			String errorMessage = String.format(
					"Could not save order outbox message with id '%s' for order with id '%s'",
					orderOutboxMessage.getId(),
					orderOutboxMessage.getPayload()
			);
			log.error(errorMessage);
			throw new PaymentDomainException(errorMessage);
		}

		log.info("OrderOutboxMessage saved with saga id '{}'", response.getSagaId());
		return response;
	}

	@Transactional
	public void saveOrderOutboxMessage(
			OrderEventPayload orderEventPayload,
			PaymentStatus paymentStatus,
			OutboxStatus outboxStatus,
			UUID sagaId
	) {
		this.save(OrderOutboxMessage.builder()
				.id(UUID.randomUUID())
				.sagaId(sagaId)
				.createdAt(orderEventPayload.getCreatedAt())
				.processedAt(ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)))
				.type(ORDER_SAGA_NAME)
				.payload(this.buildPayload(orderEventPayload))
				.paymentStatus(paymentStatus)
				.outboxStatus(outboxStatus)
				.build());
	}

	@Transactional(readOnly = true)
	public Optional<OrderOutboxMessage> getCompleted(UUID sagaId, PaymentStatus paymentStatus) {
		return this.orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
				ORDER_SAGA_NAME,
				sagaId,
				paymentStatus,
				OutboxStatus.COMPLETED
		);
	}

	@Transactional(readOnly = true)
	public Optional<List<OrderOutboxMessage>> getByOutboxStatus(OutboxStatus outboxStatus) {
		return this.orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
	}

	@Transactional
	public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
		this.orderOutboxRepository.deleteByTypeAndOutboxStatus(
				ORDER_SAGA_NAME,
				outboxStatus
		);
	}

	@Transactional
	public void updateOutboxStatus(OrderOutboxMessage orderOutboxMessage, OutboxStatus outboxStatus) {
		orderOutboxMessage.setOutboxStatus(outboxStatus);
		OrderOutboxMessage savedMessage = this.save(orderOutboxMessage);
		log.info(
				"OrderOutboxMessage with saga id '{}' is updated with outbox status '{}'",
				savedMessage.getSagaId().toString(),
				outboxStatus.name()
		);
	}

	private String buildPayload(OrderEventPayload orderEventPayload) {
		try {
			return objectMapper.writeValueAsString(orderEventPayload);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format(
					"Could not map object OrderEventPayload to JSON for order with id '%s'",
					orderEventPayload.getOrderId()
			);
			log.error(errorMessage, e);
			throw new PaymentDomainException(errorMessage, e);
		}
	}

}
