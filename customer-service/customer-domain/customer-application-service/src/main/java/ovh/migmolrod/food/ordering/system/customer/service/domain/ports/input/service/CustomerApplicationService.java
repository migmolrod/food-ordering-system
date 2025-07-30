package ovh.migmolrod.food.ordering.system.customer.service.domain.ports.input.service;

import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import ovh.migmolrod.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;

import javax.validation.Valid;

public interface CustomerApplicationService {

	CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand command);

}
