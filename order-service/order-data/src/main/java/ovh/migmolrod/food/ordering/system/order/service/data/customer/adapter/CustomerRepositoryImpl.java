package ovh.migmolrod.food.ordering.system.order.service.data.customer.adapter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.entity.CustomerEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.mapper.CustomerDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.repository.CustomerJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

	private final CustomerJpaRepository jpaRepository;
	private final CustomerDataAccessMapper mapper;

	public CustomerRepositoryImpl(
			CustomerJpaRepository jpaRepository,
			CustomerDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Optional<Customer> findCustomer(UUID customerId) {
		return this.jpaRepository.findById(customerId).map(mapper::customerEntityToCustomer);
	}

	@Override
	@Transactional
	public Customer save(Customer customer) {
		CustomerEntity savedCustomerEntity = this.jpaRepository.save(mapper.domainToEntity(customer));
		return this.mapper.customerEntityToCustomer(savedCustomerEntity);
	}

}
