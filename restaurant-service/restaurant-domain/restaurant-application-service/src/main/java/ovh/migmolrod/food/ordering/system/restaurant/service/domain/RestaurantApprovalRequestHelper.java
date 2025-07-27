package ovh.migmolrod.food.ordering.system.restaurant.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message.RestaurantApprovalRequest;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.ApprovalResponseMessagePublisher;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

	private final RestaurantDomainService restaurantDomainService;
	private final RestaurantDataMapper restaurantDataMapper;
	private final RestaurantRepository restaurantRepository;
	private final OrderApprovalRepository orderApprovalRepository;
	private final OrderOutboxHelper orderOutboxHelper;
	private final ApprovalResponseMessagePublisher approvalResponseMessagePublisher;

	public RestaurantApprovalRequestHelper(
			RestaurantDomainService restaurantDomainService,
			RestaurantDataMapper restaurantDataMapper,
			RestaurantRepository restaurantRepository,
			OrderApprovalRepository orderApprovalRepository,
			OrderOutboxHelper orderOutboxHelper,
			ApprovalResponseMessagePublisher approvalResponseMessagePublisher
	) {
		this.restaurantDomainService = restaurantDomainService;
		this.restaurantDataMapper = restaurantDataMapper;
		this.restaurantRepository = restaurantRepository;
		this.orderApprovalRepository = orderApprovalRepository;
		this.orderOutboxHelper = orderOutboxHelper;
		this.approvalResponseMessagePublisher = approvalResponseMessagePublisher;
	}

	@Transactional
	public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
		if (this.publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
			log.info("Outbox message with saga id '{}' is already saved to database", restaurantApprovalRequest.getSagaId());
			return;
		}

		log.info("Processing restaurant approval for order id '{}'", restaurantApprovalRequest.getOrderId());
		List<String> failureMessages = new ArrayList<>();
		Restaurant restaurant = this.findRestaurant(restaurantApprovalRequest);

		OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);
		orderApprovalRepository.save(orderApprovalEvent.getOrderApproval());

		this.orderOutboxHelper.saveOrderOutboxMessage(
				restaurantDataMapper.approvalEventToOrderEventPayload(orderApprovalEvent),
				orderApprovalEvent.getOrderApproval().getApprovalStatus(),
				OutboxStatus.STARTED,
				UUID.fromString(restaurantApprovalRequest.getSagaId())
		);
	}

	private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
		Restaurant restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
		Restaurant restaurantInformation = restaurantRepository.findRestaurantInformation(restaurant)
				.orElseThrow(() -> {
					String errorMessage = String.format(
							"Restaurant with id %s not found!", restaurantApprovalRequest.getRestaurantId()
					);
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

	private boolean publishIfOutboxMessageProcessed(RestaurantApprovalRequest request) {
		Optional<OrderOutboxMessage> message = this.orderOutboxHelper.getCompleted(UUID.fromString(request.getSagaId()));
		if (message.isPresent()) {
			this.approvalResponseMessagePublisher.publish(message.get(), this.orderOutboxHelper::updateOutboxStatus);
			return true;
		}
		return false;
	}

}
