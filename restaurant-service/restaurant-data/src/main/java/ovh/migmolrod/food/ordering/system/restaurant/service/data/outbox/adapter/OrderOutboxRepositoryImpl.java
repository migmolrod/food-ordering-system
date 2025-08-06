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

	private final OrderOutboxJpaRepository jpaRepository;
	private final OrderOutboxDataMapper mapper;

	public OrderOutboxRepositoryImpl(
			OrderOutboxJpaRepository jpaRepository,
			OrderOutboxDataMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
		OrderOutboxEntity entity = this.mapper.messageToEntity(orderOutboxMessage);
		OrderOutboxEntity savedOrderOutboxEntity = this.jpaRepository.save(entity);

		return this.mapper.entityToMessage(savedOrderOutboxEntity);
	}

	@Override
	public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
		return Optional.of(this.jpaRepository.findByTypeAndOutboxStatus(
						type,
						outboxStatus
				).orElseThrow(() -> new OrderOutboxNotFoundException("Approval outbox object not found for saga type " + type))
				.stream()
				.map(mapper::entityToMessage)
				.collect(Collectors.toList()));
	}

	@Override
	public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(
			String type,
			UUID sagaId,
			OutboxStatus outboxStatus
	) {
		return this.jpaRepository.findByTypeAndSagaIdAndOutboxStatus(
				type,
				sagaId,
				outboxStatus
		).map(mapper::entityToMessage);
	}

	@Override
	public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
		this.jpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
	}

}
