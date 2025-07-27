package ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.exception.RestaurantDomainException;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

	@Transactional(readOnly = true)
	public Optional<OrderOutboxMessage> getCompleted(UUID sagaId) {
		return this.orderOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(
				ORDER_SAGA_NAME,
				sagaId,
				OutboxStatus.COMPLETED
		);
	}

	@Transactional(readOnly = true)
	public Optional<List<OrderOutboxMessage>> getByOutboxStatus(OutboxStatus outboxStatus) {
		return this.orderOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
	}

	@Transactional
	public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
		this.orderOutboxRepository.deleteByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus);
	}

	@Transactional
	public void saveOrderOutboxMessage(
			OrderEventPayload payload,
			OrderApprovalStatus approvalStatus,
			OutboxStatus outboxStatus,
			UUID sagaId
	) {
		OrderOutboxMessage message = OrderOutboxMessage.builder()
				.id(UUID.randomUUID())
				.sagaId(sagaId)
				.type(ORDER_SAGA_NAME)
				.payload(this.buildPayload(payload))
				.approvalStatus(approvalStatus)
				.outboxStatus(outboxStatus)
				.build();

		this.save(message);
	}

	@Transactional
	public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
		return this.orderOutboxRepository.save(orderOutboxMessage);
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
			throw new RestaurantDomainException(errorMessage, e);
		}
	}

}
