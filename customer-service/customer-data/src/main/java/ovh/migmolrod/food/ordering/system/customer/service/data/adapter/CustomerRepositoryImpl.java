package ovh.migmolrod.food.ordering.system.customer.service.data.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.customer.service.data.mapper.CustomerDataMapper;
import ovh.migmolrod.food.ordering.system.customer.service.data.repository.CustomerJpaRepository;
import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

	private final CustomerJpaRepository jpaRepository;
	private final CustomerDataMapper mapper;

	public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository, CustomerDataMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Customer createCustomer(Customer customer) {
		return this.mapper.entityToDomain(this.jpaRepository.save(this.mapper.domainToEntity(customer)));
	}

}
