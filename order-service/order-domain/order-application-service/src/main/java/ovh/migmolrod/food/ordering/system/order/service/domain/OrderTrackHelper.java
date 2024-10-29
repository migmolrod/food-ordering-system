package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

@Slf4j
@Component
public class OrderTrackHelper {

	private final OrderRepository orderRepository;

	public OrderTrackHelper(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Transactional(readOnly = true)
	public Order trackOrder(TrackingId trackingId) {
		Optional<Order> order = this.orderRepository.findByTrackingId(trackingId);

		if (order.isEmpty()) {
			String errorMessage = String.format("Order with tracking id '%s' could not be found",
					trackingId.getValue());
			log.warn(errorMessage);
			throw new OrderNotFoundException(errorMessage);
		}

		return order.get();
	}

}
