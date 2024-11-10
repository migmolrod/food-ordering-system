package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

	private final RestaurantDomainService restaurantDomainService;
	private final RestaurantDataMapper restaurantDataMapper;
	private final RestaurantRepository restaurantRepository;
	private final OrderApprovalRepository orderApprovalRepository;
	private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
	private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

	public RestaurantApprovalRequestHelper(
			RestaurantDomainService restaurantDomainService,
			RestaurantDataMapper restaurantDataMapper,
			RestaurantRepository restaurantRepository,
			OrderApprovalRepository orderApprovalRepository,
			OrderApprovedMessagePublisher orderApprovedMessagePublisher,
			OrderRejectedMessagePublisher orderRejectedMessagePublisher
	) {
		this.restaurantDomainService = restaurantDomainService;
		this.restaurantDataMapper = restaurantDataMapper;
		this.restaurantRepository = restaurantRepository;
		this.orderApprovalRepository = orderApprovalRepository;
		this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
		this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
	}

	@Transactional
	public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
		log.info("Processing restaurant approval for order id {}", restaurantApprovalRequest.getOrderId());
		List<String> failureMessages = new ArrayList<>();
		Restaurant restaurant = findRestaurant(restaurantApprovalRequest);

		OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
				restaurant,
				failureMessages,
				orderApprovedMessagePublisher,
				orderRejectedMessagePublisher
		);
		orderApprovalRepository.save(orderApprovalEvent.getOrderApproval());

		return orderApprovalEvent;
	}

	private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
		Restaurant restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
		Restaurant restaurantInformation = restaurantRepository.findRestaurantInformation(restaurant).orElseThrow(() -> {
			String errorMessage = "Restaurant with id " + restaurantApprovalRequest.getRestaurantId() + " not found!";
			log.error(errorMessage);
			return new RestaurantNotFoundException(errorMessage);
		});

		restaurant.setActive(restaurantInformation.isActive());
		restaurant.getOrderDetail().getProducts().forEach(product -> {
			restaurantInformation.getOrderDetail().getProducts().forEach(p -> {
				if (p.getId().equals(product.getId())) {
					product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
				}
			});
		});
		restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

		return restaurant;
	}

}
