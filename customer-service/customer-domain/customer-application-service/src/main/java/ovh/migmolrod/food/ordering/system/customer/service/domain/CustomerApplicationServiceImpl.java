package ovh.migmolrod.food.ordering.system.customer.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import ovh.migmolrod.food.ordering.system.customer.service.domain.handlers.CreateCustomerCommandHandler;
import ovh.migmolrod.food.ordering.system.customer.service.domain.mapper.CustomerMapper;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.output.messaging.publisher.CustomerMessagePublisher;

@Slf4j
@Validated
@Service
public class CustomerApplicationServiceImpl implements CustomerApplicationService {

	private final CreateCustomerCommandHandler handler;
	private final CustomerMapper mapper;
	private final CustomerMessagePublisher publisher;

	public CustomerApplicationServiceImpl(
			CreateCustomerCommandHandler handler,
			CustomerMapper mapper,
			CustomerMessagePublisher publisher
	) {
		this.handler = handler;
		this.mapper = mapper;
		this.publisher = publisher;
	}

	@Override
	public CreateCustomerResponse createCustomer(CreateCustomerCommand command) {
		CustomerCreatedEvent event = this.handler.createCustomer(command);
		this.publisher.publish(event);
		String message = String.format("Customer with id '%s' saved successfully", command.getCustomerId().toString());

		return this.mapper.customerToCreateCustomerResponse(event.getCustomer(), message);
	}

}
