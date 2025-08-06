package ovh.migmolrod.food.ordering.system.order.service.data.order.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.order.mapper.OrderDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.order.repository.OrderJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository jpaRepository;
	private final OrderDataAccessMapper mapper;

	public OrderRepositoryImpl(
			OrderJpaRepository jpaRepository,
			OrderDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Order save(Order order) {
		OrderEntity savedOrderEntity = jpaRepository.save(mapper.orderToOrderEntity(order));

		return mapper.orderEntityToOrder(savedOrderEntity);
	}

	@Override
	public Optional<Order> findByTrackingId(TrackingId trackingId) {
		return jpaRepository.findByTrackingId(trackingId.getValue()).map(mapper::orderEntityToOrder);
	}

	@Override
	public Optional<Order> findById(OrderId orderId) {
		return jpaRepository.findById(orderId.getValue()).map(mapper::orderEntityToOrder);
	}

}
