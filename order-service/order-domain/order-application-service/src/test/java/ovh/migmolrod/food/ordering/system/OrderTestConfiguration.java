package ovh.migmolrod.food.ordering.system;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainService;
import ovh.migmolrod.food.ordering.system.order.service.domain.OrderDomainServiceImpl;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.approval.ApprovalRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

@SpringBootApplication(scanBasePackages = "ovh.migmolrod.food.ordering.system")
public class OrderTestConfiguration {

	@Bean
	public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
		return Mockito.mock(PaymentRequestMessagePublisher.class);
	}

	@Bean
	public ApprovalRequestMessagePublisher approvalRequestMessagePublisher() {
		return Mockito.mock(ApprovalRequestMessagePublisher.class);
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
