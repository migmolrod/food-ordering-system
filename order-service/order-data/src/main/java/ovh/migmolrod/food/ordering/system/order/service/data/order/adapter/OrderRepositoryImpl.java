package ovh.migmolrod.food.ordering.system.order.service.data.order.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.order.mapper.OrderDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.order.repository.OrderJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository orderJpaRepository;
	private final OrderDataAccessMapper orderDataAccessMapper;

	public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
		this.orderJpaRepository = orderJpaRepository;
		this.orderDataAccessMapper = orderDataAccessMapper;
	}

	@Override
	public Order save(Order order) {
		OrderEntity savedOrderEntity = orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order));

		return orderDataAccessMapper.orderEntityToOrder(savedOrderEntity);
	}

	@Override
	public Optional<Order> findByTrackingId(TrackingId trackingId) {
		return orderJpaRepository.findByTrackingId(trackingId.getValue()).map(orderDataAccessMapper::orderEntityToOrder);
	}

}
