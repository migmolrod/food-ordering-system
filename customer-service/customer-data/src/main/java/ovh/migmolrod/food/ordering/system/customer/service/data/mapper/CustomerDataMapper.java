package ovh.migmolrod.food.ordering.system.customer.service.data.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.customer.service.data.entity.CustomerEntity;
import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;

@Component
public class CustomerDataMapper {

	public Customer entityToDomain(CustomerEntity entity) {
		return new Customer(
				new CustomerId(entity.getId()),
				entity.getUsername(),
				entity.getFirstName(),
				entity.getLastName()
		);
	}

	public CustomerEntity domainToEntity(Customer domain) {
		return CustomerEntity.builder()
				.id(domain.getId().getValue())
				.username(domain.getUsername())
				.firstName(domain.getFirstName())
				.lastName(domain.getLastName())
				.build();
	}

}
