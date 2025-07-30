package ovh.migmolrod.food.ordering.system.customer.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;

@Component
public class CustomerMapper {

	public Customer customerCommandToCustomer(CreateCustomerCommand command) {
		return new Customer(
				new CustomerId(command.getCustomerId()),
				command.getUsername(),
				command.getFirstName(),
				command.getLastName()
		);
	}

	public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
		return new CreateCustomerResponse(customer.getId().getValue(), message);
	}

}
