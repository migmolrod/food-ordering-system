package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

	OrderApprovalEvent validateOrder(
			Restaurant restaurant,
			List<String> failureMessages,
			DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
			DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher
	);

}
