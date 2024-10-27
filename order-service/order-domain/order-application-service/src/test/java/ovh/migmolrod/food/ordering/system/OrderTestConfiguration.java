package ovh.migmolrod.food.ordering.system;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainServiceImpl;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

@SpringBootApplication(scanBasePackages = "ovh.migmolrod.food.ordering.system")
public class OrderTestConfiguration {

	@Bean
	public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
		return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
	}

	@Bean
	public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
		return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
	}

	@Bean
	public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
		return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
	}

	@Bean
	public OrderRepository orderRepository() {
		return Mockito.mock(OrderRepository.class);
	}

	@Bean
	public CustomerRepository customerRepository() {
		return Mockito.mock(CustomerRepository.class);
	}

	@Bean
	public RestaurantRepository restaurantRepository() {
		return Mockito.mock(RestaurantRepository.class);
	}

	@Bean
	public OrderDomainService orderDomainService() {
		return new OrderDomainServiceImpl();
	}
}
