package ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository;

import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findById(OrderId orderId);

	Optional<Order> findByTrackingId(TrackingId trackingId);

}
