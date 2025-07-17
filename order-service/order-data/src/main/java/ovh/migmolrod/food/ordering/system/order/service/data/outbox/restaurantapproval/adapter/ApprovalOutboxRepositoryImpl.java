package ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.adapter;

import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.entity.ApprovalOutboxEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.mapper.ApprovalOutboxDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

	private final ApprovalOutboxJpaRepository approvalOutboxJpaRepository;
	private final ApprovalOutboxDataMapper dataMapper;

	public ApprovalOutboxRepositoryImpl(
			ApprovalOutboxJpaRepository approvalOutboxJpaRepository,
			ApprovalOutboxDataMapper dataMapper
	) {
		this.approvalOutboxJpaRepository = approvalOutboxJpaRepository;
		this.dataMapper = dataMapper;
	}

	@Override
	public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage message) {
		ApprovalOutboxEntity savedEntity = this.approvalOutboxJpaRepository.save(dataMapper.messageToEntity(message));

		return dataMapper.entityToMessage(savedEntity);
	}

	@Override
	public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		return Optional.of(this.approvalOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
						type,
						outboxStatus,
						Arrays.asList(sagaStatus)
				).orElseThrow(() -> new ApprovalOutboxNotFoundException("Approval outbox object not found for saga type " + type))
				.stream()
				.map(dataMapper::entityToMessage)
				.collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
			String type,
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
				.map(dataMapper::entityToMessage);
	}

	@Override
	public void deleteByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		this.approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
				type,
				outboxStatus,
				Arrays.asList(sagaStatus)
		);
	}

}
