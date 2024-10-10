package ovh.migmolrod.food.ordering.system.order.service.domain.helper.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.exception.DomainException;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {

	private final OrderDomainService orderDomainService;
	private final OrderRepository orderRepository;
	private final CustomerRepository customerRepository;
	private final RestaurantRepository restaurantRepository;
	private final OrderDataMapper orderDataMapper;

	public OrderCreateCommandHandler(
			OrderDomainService orderDomainService,
			OrderRepository orderRepository,
			CustomerRepository customerRepository,
			RestaurantRepository restaurantRepository,
			OrderDataMapper orderDataMapper
	) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.customerRepository = customerRepository;
		this.restaurantRepository = restaurantRepository;
		this.orderDataMapper = orderDataMapper;
	}

	@Transactional
	public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
		checkCustomer(createOrderCommand.getCustomerId());
		Restaurant restaurant = checkRestaurant(createOrderCommand);
		Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
		OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
		Order savedOrder = saveOrder(order);
		log.info("Order command successful for order with id {}", savedOrder.getId().getValue());

		return orderDataMapper.orderToCreateOrderResponse(savedOrder);
	}

	private void checkCustomer(UUID customerId) {
		Optional<Customer> customer = this.customerRepository.findCustomer(customerId);

		if (customer.isEmpty()) {
			String errorMessage = String.format("Customer with id %s not found", customerId);
			log.warn(errorMessage);
			throw new DomainException(errorMessage);
		}
	}

	private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
		Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
		Optional<Restaurant> restaurantOptional = this.restaurantRepository.findRestaurantInformation(restaurant);

		if (restaurantOptional.isEmpty()) {
			String errorMessage = String.format("Restaurant with id %s not found", restaurant.getId());
			log.warn(errorMessage);
			throw new DomainException(errorMessage);
		}

		return restaurantOptional.get();
	}

	private Order saveOrder(Order order) {
		Order savedOrder = orderRepository.save(order);
		if (savedOrder == null) {
			String errorMessage = String.format("Order for restaurant %s could not be saved", order.getRestaurantId());
			log.warn(errorMessage);
			throw new DomainException(errorMessage);
		}
		log.info("Order has ben saved with ID {}", savedOrder.getId().getValue());

		return savedOrder;
	}

}
