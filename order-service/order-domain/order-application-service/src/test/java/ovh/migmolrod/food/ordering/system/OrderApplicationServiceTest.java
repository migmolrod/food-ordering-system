package ovh.migmolrod.food.ordering.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ovh.migmolrod.food.ordering.system.domain.valueobject.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.OrderItem;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

	@Autowired
	private OrderApplicationService orderApplicationService;

	@Autowired
	private OrderDataMapper orderDataMapper;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	private CreateOrderCommand createOrderCommand;
	private CreateOrderCommand createOrderCommandWrongPrice;
	private CreateOrderCommand createOrderCommandWrongProductPrice;
	private final UUID CUSTOMER_ID = UUID.fromString("eff9a4ce-a18a-41d5-bf0a-c245e07e8b68");
	private final UUID RESTAURANT_ID = UUID.fromString("39442cde-b7b9-4063-9e98-07a6e58f55f4");
	private final UUID PRODUCT_ID = UUID.fromString("6f31dd5a-93fb-4370-b187-f50e084ea186");
	private final UUID ORDER_ID = UUID.fromString("c428f69d-1bfc-4e6e-854f-9f9e898d1f61");
	private final BigDecimal PRICE = new BigDecimal("200.00");

	@BeforeAll
	public void init() {
		createOrderCommand = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street 1")
						.postalCode("28800")
						.city("Madrid")
						.build())
				.price(PRICE)
				.items(List.of(
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(1)
										.price(new BigDecimal("50.00"))
										.subTotal(new BigDecimal("50.00"))
										.build(),
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(3)
										.price(new BigDecimal("50.00"))
										.subTotal(new BigDecimal("150.00"))
										.build()
						)
				)
				.build();

		createOrderCommandWrongPrice = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street 1")
						.postalCode("28800")
						.city("Madrid")
						.build())
				.price(new BigDecimal("250.00"))
				.items(List.of(
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(1)
										.price(new BigDecimal("50.00"))
										.subTotal(new BigDecimal("50.00"))
										.build(),
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(3)
										.price(new BigDecimal("50.00"))
										.subTotal(new BigDecimal("150.00"))
										.build()
						)
				)
				.build();

		createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street 1")
						.postalCode("28800")
						.city("Madrid")
						.build())
				.price(new BigDecimal("210.00"))
				.items(List.of(
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(1)
										.price(new BigDecimal("60.00"))
										.subTotal(new BigDecimal("60.00"))
										.build(),
								OrderItem.builder()
										.productId(PRODUCT_ID)
										.quantity(3)
										.price(new BigDecimal("50.00"))
										.subTotal(new BigDecimal("150.00"))
										.build()
						)
				)
				.build();

		Customer customer = new Customer();
		customer.setId(new CustomerId(CUSTOMER_ID));

		Restaurant restaurantResponse = Restaurant.builder()
				.restaurantId(new RestaurantId(RESTAURANT_ID))
				.products(List.of(
								new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
								new Product(new ProductId(UUID.randomUUID()), "product-2", new Money(new BigDecimal("66.00")))
						)
				)
				.active(true)
				.build();

		Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
		order.setId(new OrderId(ORDER_ID));

		Mockito.when(customerRepository.findCustomer(CUSTOMER_ID))
				.thenReturn(Optional.of(customer));

		Mockito.when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
				.thenReturn(Optional.of(restaurantResponse));

		Mockito.when(orderRepository.save(Mockito.any(Order.class)))
				.thenReturn(order);
	}

	@Test
	public void testCreateOrderOk() {
		CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
		Assertions.assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
		Assertions.assertEquals("Order created successfully", createOrderResponse.getMessage());
		Assertions.assertNotNull(createOrderResponse.getOrderTrackingId());
	}

	@Test
	public void testCreateOrderWrongPrice() {
		OrderDomainException orderDomainException = Assertions.assertThrows(
				OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommandWrongPrice)
		);
		Assertions.assertEquals(
				orderDomainException.getMessage(),
				"Total price (250.00) does not match the sum of all items prices (200.00)"
		);
	}

	@Test
	public void testCreateOrderWrongProductPrice() {
		OrderDomainException orderDomainException = Assertions.assertThrows(
				OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice)
		);
		Assertions.assertEquals(
				"Order item price (60.00) is not valid for product " + PRODUCT_ID,
				orderDomainException.getMessage()
		);
	}

	@Test
	public void testCreateOrderInactiveRestaurant() {
		Restaurant restaurantResponse = Restaurant.builder()
				.restaurantId(new RestaurantId(RESTAURANT_ID))
				.products(List.of(
								new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
								new Product(new ProductId(UUID.randomUUID()), "product-2", new Money(new BigDecimal("66.00")))
						)
				)
				.active(false)
				.build();
		Mockito.when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
				.thenReturn(Optional.of(restaurantResponse));

		OrderDomainException orderDomainException = Assertions.assertThrows(OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommand));
		Assertions.assertEquals(
				"Restaurant with id " + RESTAURANT_ID + " is currently not " + "active",
				orderDomainException.getMessage()
		);
	}

}
