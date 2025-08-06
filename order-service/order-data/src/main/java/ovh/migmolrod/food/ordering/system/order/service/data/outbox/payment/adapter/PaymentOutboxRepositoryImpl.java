package ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.entity.PaymentOutboxEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.exception.PaymentOutboxNotFoundException;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.mapper.PaymentOutboxDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.repository.PaymentOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.saga.SagaStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

	private final PaymentOutboxJpaRepository jpaRepository;
	private final PaymentOutboxDataMapper mapper;

	public PaymentOutboxRepositoryImpl(
			PaymentOutboxJpaRepository jpaRepository,
			PaymentOutboxDataMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage message) {
		PaymentOutboxEntity savedEntity = this.jpaRepository.save(mapper.messageToEntity(message));

		return mapper.entityToMessage(savedEntity);
	}

	@Override
	public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		return Optional.of(this.jpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
						type,
						outboxStatus,
						Arrays.asList(sagaStatus)
				).orElseThrow(() -> new PaymentOutboxNotFoundException("Payment outbox object not found for saga type " + type))
				.stream()
				.map(mapper::entityToMessage)
				.collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
			String type,
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return jpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
				.map(mapper::entityToMessage);
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
				Arrays.asList(sagaStatus)
		);
	}

}
