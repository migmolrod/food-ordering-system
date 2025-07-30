package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.CustomerCreatedModel;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.customer.CustomerCreatedMessageListener;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;

@Slf4j
@Service
public class CustomerCreatedMessageListenerImpl implements CustomerCreatedMessageListener {

	private final CustomerRepository repository;
	private final OrderDataMapper mapper;

	public CustomerCreatedMessageListenerImpl(CustomerRepository repository, OrderDataMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void customerCreated(CustomerCreatedModel model) {
		Customer customer = this.repository.save(this.mapper.customerCreatedMessageToCustomer(model));
		if (customer == null) {
			String errorMessage = String.format("Customer with id '%s' could not be saved to database", model.getId());
			log.error(errorMessage);
			throw new OrderDomainException(errorMessage);
		}
		log.info("Customer with id {} has been saved to database", customer.getId());
	}

}
