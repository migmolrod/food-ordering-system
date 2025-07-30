package ovh.migmolrod.food.ordering.system.customer.service.domain;

import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

	CustomerCreatedEvent validateAndInitializeCustomer(Customer customer);

}
