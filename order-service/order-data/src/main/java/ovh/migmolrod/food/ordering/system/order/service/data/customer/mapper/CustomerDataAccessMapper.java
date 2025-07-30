package ovh.migmolrod.food.ordering.system.order.service.data.customer.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.entity.CustomerEntity;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;

@Component
public class CustomerDataAccessMapper {

	public Customer customerEntityToCustomer(CustomerEntity entity) {
		CustomerId id = new CustomerId(entity.getId());
		return new Customer(id, entity.getUsername(), entity.getFirstName(), entity.getLastName());
	}

	public CustomerEntity domainToEntity(Customer customer) {
		return CustomerEntity.builder()
				.id(customer.getId().getValue())
				.username(customer.getUsername())
				.firstName(customer.getFirstName())
				.lastName(customer.getLastName())
				.build();
	}

}
