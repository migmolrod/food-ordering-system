package ovh.migmolrod.food.ordering.system.customer.service.domain.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.customer.service.domain.CustomerDomainService;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import ovh.migmolrod.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import ovh.migmolrod.food.ordering.system.customer.service.domain.mapper.CustomerMapper;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;

@Slf4j
@Component
public class CreateCustomerCommandHandler {

	private final CustomerDomainService service;
	private final CustomerRepository repository;
	private final CustomerMapper mapper;

	public CreateCustomerCommandHandler(
			CustomerDomainService service,
			CustomerRepository repository,
			CustomerMapper mapper
	) {
		this.service = service;
		this.repository = repository;
		this.mapper = mapper;
	}

	@Transactional
	public CustomerCreatedEvent createCustomer(CreateCustomerCommand command) {
		Customer customer = this.mapper.customerCommandToCustomer(command);
		CustomerCreatedEvent event = this.service.validateAndInitializeCustomer(customer);
		Customer savedCustomer = this.repository.createCustomer(customer);
		if (savedCustomer == null) {
			String errorMessage = String.format("Customer with id '%s' could not be created",
					command.getCustomerId().toString());
			log.error(errorMessage);
			throw new CustomerDomainException(errorMessage);
		}
		log.info("Customer with id '{}' has been created", savedCustomer.getId().getValue());

		return event;
	}

}
