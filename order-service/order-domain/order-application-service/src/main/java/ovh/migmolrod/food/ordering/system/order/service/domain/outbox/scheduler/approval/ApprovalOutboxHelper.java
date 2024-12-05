package ovh.migmolrod.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ovh.migmolrod.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class ApprovalOutboxHelper {

	private final ApprovalOutboxRepository approvalOutboxRepository;
	private final ObjectMapper objectMapper;

	public ApprovalOutboxHelper(
			ApprovalOutboxRepository approvalOutboxRepository,
			ObjectMapper objectMapper
	) {
		this.approvalOutboxRepository = approvalOutboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
				ORDER_SAGA_NAME,
				outboxStatus,
				sagaStatus
		);
	}

	@Transactional(readOnly = true)
	public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
				ORDER_SAGA_NAME,
				sagaId,
				sagaStatus
		);
	}

	@Transactional
	public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
				ORDER_SAGA_NAME,
				outboxStatus,
				sagaStatus
		);
	}

	@Transactional
	public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
		OrderApprovalOutboxMessage response = approvalOutboxRepository.save(orderApprovalOutboxMessage);
		if (response == null) {
			String errorMessage = String.format(
					"Could not save OrderApprovalOutboxMessage with outbox id: %s",
					orderApprovalOutboxMessage.getId().toString()
			);
			log.error(errorMessage);
			throw new OrderDomainException(errorMessage);
		}

		log.info("OrderApprovalOutboxMessage saved with outbox id: {}", response.getId());
		return response;
	}

	@Transactional
	public void saveApprovalOutboxMessage(
			OrderApprovalEventPayload orderApprovalEventPayload,
			OrderStatus orderStatus,
			SagaStatus sagaStatus,
			OutboxStatus outboxStatus,
			UUID sagaId
	) {
		this.save(OrderApprovalOutboxMessage.builder()
				.id(UUID.randomUUID())
				.sagaId(sagaId)
				.createdAt(orderApprovalEventPayload.getCreatedAt())
				.type(ORDER_SAGA_NAME)
				.payload(buildPayload(orderApprovalEventPayload))
				.sagaStatus(sagaStatus)
				.outboxStatus(outboxStatus)
				.orderStatus(orderStatus)
				.build());
	}

	private String buildPayload(OrderApprovalEventPayload orderApprovalEventPayload) {
		try {
			return objectMapper.writeValueAsString(orderApprovalEventPayload);
		} catch (JsonProcessingException e) {
			String errorMessage = String.format(
					"Could not map object orderApprovalEventPayload to JSON for order with id: %s",
					orderApprovalEventPayload.getOrderId()
			);
			log.error(errorMessage, e);
			throw new OrderDomainException(errorMessage, e);
		}
	}

}
