package ovh.migmolrod.food.ordering.system.order.service.data.customer.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.entity.CustomerEntity;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;

@Component
public class CustomerDataAccessMapper {

	public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
		return new Customer(new CustomerId(customerEntity.getId()));
	}

}
