package ovh.migmolrod.food.ordering.system.customer.service.domain.event;

import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class CustomerEvent implements DomainEvent<Customer> {

	private final Customer customer;
	private final ZonedDateTime createdAt;

	public CustomerEvent(Customer customer, ZonedDateTime createdAt) {
		this.customer = customer;
		this.createdAt = createdAt;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public ZonedDateTime getCreatedAt() {
		return this.createdAt;
	}

}
