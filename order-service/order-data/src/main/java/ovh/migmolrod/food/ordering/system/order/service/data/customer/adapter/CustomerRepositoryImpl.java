package ovh.migmolrod.food.ordering.system.order.service.data.customer.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.mapper.CustomerDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.customer.repository.CustomerJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

	private final CustomerJpaRepository customerJpaRepository;
	private final CustomerDataAccessMapper customerDataAccessMapper;

	public CustomerRepositoryImpl(
			CustomerJpaRepository customerJpaRepository,
			CustomerDataAccessMapper customerDataAccessMapper
	) {
		this.customerJpaRepository = customerJpaRepository;
		this.customerDataAccessMapper = customerDataAccessMapper;
	}

	@Override
	public Optional<Customer> findCustomer(UUID customerId) {
		return this.customerJpaRepository.findById(customerId).map(customerDataAccessMapper::customerEntityToCustomer);
	}

}
