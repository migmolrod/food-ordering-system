package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import lombok.extern.slf4j.Slf4j;
import ovh.migmolrod.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

	@Override
	public OrderApprovalEvent validateOrder(
			Restaurant restaurant,
			List<String> failureMessages,
			DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
			DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher
	) {
		log.info("Validating order with id {}", restaurant.getOrderDetail().getId().getValue());
		restaurant.validateOrder(failureMessages);

		if (failureMessages.isEmpty()) {
			log.info("Approved order with id {}", restaurant.getOrderDetail().getId().getValue());
			restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);

			return new OrderApprovedEvent(
					restaurant.getOrderApproval(),
					restaurant.getId(),
					ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)),
					failureMessages
			);
		} else {
			log.info("Rejected order with id {}", restaurant.getOrderDetail().getId().getValue());
			restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);

			return new OrderRejectedEvent(
					restaurant.getOrderApproval(),
					restaurant.getId(),
					ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)),
					failureMessages
			);
		}
	}

}
