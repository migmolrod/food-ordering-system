package ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.entity.OrderOutboxEntity;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.exception.OrderOutboxNotFoundException;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.mapper.OrderOutboxDataMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.outbox.repository.OrderOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

	private final OrderOutboxJpaRepository orderOutboxJpaRepository;
	private final OrderOutboxDataMapper orderOutboxDataMapper;

	public OrderOutboxRepositoryImpl(
			OrderOutboxJpaRepository orderOutboxJpaRepository,
			OrderOutboxDataMapper orderOutboxDataMapper
	) {
		this.orderOutboxJpaRepository = orderOutboxJpaRepository;
		this.orderOutboxDataMapper = orderOutboxDataMapper;
	}

	@Override
	public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
		OrderOutboxEntity entity = this.orderOutboxDataMapper.messageToEntity(orderOutboxMessage);
		OrderOutboxEntity savedOrderOutboxEntity = this.orderOutboxJpaRepository.save(entity);

		return this.orderOutboxDataMapper.entityToMessage(savedOrderOutboxEntity);
	}

	@Override
	public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
		return Optional.of(this.orderOutboxJpaRepository.findByTypeAndOutboxStatus(
						type,
						outboxStatus
				).orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object not found for saga type " + type))
				.stream()
				.map(orderOutboxDataMapper::entityToMessage)
				.collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
			String type,
			UUID sagaId,
			OutboxStatus outboxStatus
	) {
		return this.orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(
				type,
				sagaId,
				outboxStatus
		).map(orderOutboxDataMapper::entityToMessage);
	}

	@Override
	public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
		this.orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
	}

}
