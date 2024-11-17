package ovh.migmolrod.food.ordering.system.order.service.domain.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

	private final OrderRepository orderRepository;

	public OrderSagaHelper(OrderRepository orderRepository) {this.orderRepository = orderRepository;}

	public Order findOrder(String orderId) {
		Optional<Order> order = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
		if (order.isEmpty()) {
			String errorMessage = String.format("Order with id %s could not be found!", orderId);
			log.error(errorMessage);
			throw new OrderNotFoundException(errorMessage);
		}
		return order.get();
	}

	void saveOrder(Order order) {
		orderRepository.save(order);
	}

}
