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

	private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
	private final PaymentOutboxDataMapper dataMapper;

	public PaymentOutboxRepositoryImpl(
			PaymentOutboxJpaRepository paymentOutboxJpaRepository,
			PaymentOutboxDataMapper dataMapper
	) {
		this.paymentOutboxJpaRepository = paymentOutboxJpaRepository;
		this.dataMapper = dataMapper;
	}

	@Override
	public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage message) {
		PaymentOutboxEntity savedEntity = this.paymentOutboxJpaRepository.save(dataMapper.messageToEntity(message));

		return dataMapper.entityToMessage(savedEntity);
	}

	@Override
	public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		return Optional.of(this.paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
						type,
						outboxStatus,
						Arrays.asList(sagaStatus)
				).orElseThrow(() -> new PaymentOutboxNotFoundException("Payment outbox object not found for saga type " + type))
				.stream()
				.map(dataMapper::entityToMessage)
				.collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSagaStatus(
			String type,
			UUID sagaId,
			SagaStatus... sagaStatus
	) {
		return paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, Arrays.asList(sagaStatus))
				.map(dataMapper::entityToMessage);
	}

	@Override
	public void deleteByTypeAndOutboxStatusAndSagaStatus(
			String type,
			OutboxStatus outboxStatus,
			SagaStatus... sagaStatus
	) {
		this.paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
				type,
				outboxStatus,
				Arrays.asList(sagaStatus)
		);
	}

}
