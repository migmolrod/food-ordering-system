package ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository;

import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {

	Optional<Customer> findCustomer(CustomerId customerId);

}
