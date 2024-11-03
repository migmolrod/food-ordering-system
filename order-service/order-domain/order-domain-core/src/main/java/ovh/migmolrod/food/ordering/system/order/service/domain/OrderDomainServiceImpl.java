package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

	@Override
	public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
		validateRestaurant(restaurant);
		setOrderProductInformation(order, restaurant);
		order.validateOrder();
		order.initializeOrder();
		log.info("Order with id '{}' is initialized", order.getId().getValue());

		return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
	}

	@Override
	public OrderPaidEvent payOrder(Order order) {
		order.pay();
		log.info("Order with id '{}' has been paid", order.getId().getValue());

		return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
	}

	@Override
	public void approveOrder(Order order) {
		order.approve();
		log.info("Order with id '{}' has been approved", order.getId().getValue());
	}

	@Override
	public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
		order.initCancel(failureMessages);
		log.info("Payment for order with id '{}' has been cancelled", order.getId().getValue());

		return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
	}

	@Override
	public void cancelOrder(Order order, List<String> failureMessages) {
		order.cancel(failureMessages);
		log.info("Order with id '{}' has been cancelled", order.getId().getValue());
	}

	private void validateRestaurant(Restaurant restaurant) {
		if (!restaurant.isActive()) {
			throw new OrderDomainException(String.format("Restaurant with id %s is currently not active",
					restaurant.getId().getValue()));
		}
	}

	private void setOrderProductInformation(Order order, Restaurant restaurant) {
		order.getItems().forEach(orderItem -> restaurant.getProducts().forEach(restaurantProduct -> {
					Product currentProduct = orderItem.getProduct();
					if (currentProduct.equals(restaurantProduct)) {
						currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(),
								restaurantProduct.getPrice());
					}
				})
		);
	}

}
