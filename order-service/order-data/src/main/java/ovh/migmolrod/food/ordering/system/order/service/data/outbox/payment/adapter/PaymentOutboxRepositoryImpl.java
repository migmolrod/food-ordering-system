package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.adapter;

import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.entity.PaymentOutboxEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.mapper.PaymentOutboxDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.repository.PaymentOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

	private final PaymentOutboxJpaRepository jpaRepository;
	private final PaymentOutboxDataMapper dataMapper;

	public PaymentOutboxRepositoryImpl(PaymentOutboxJpaRepository jpaRepository, PaymentOutboxDataMapper dataMapper) {
		this.jpaRepository = jpaRepository;
		this.dataMapper = dataMapper;
	}

	@Override
	public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage message) {
		PaymentOutboxEntity savedEntity = this.jpaRepository.save(dataMapper.messageToEntity(message));

		return dataMapper.entityToMessage(savedEntity);
	}

	@Override
	public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		Optional<List<PaymentOutboxEntity>> entities = this.jpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
				type,
				outboxStatus,
				List.of(sagaStatus)
		);

		return entities.map(paymentOutboxEntities -> paymentOutboxEntities.stream().map(dataMapper::entityToMessage).collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
			String type,
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return Optional.empty();
	}

	@Override
	public void deleteByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		this.jpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
				type,
				outboxStatus,
				List.of(sagaStatus)
		);
	}

}
