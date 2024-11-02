package ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
