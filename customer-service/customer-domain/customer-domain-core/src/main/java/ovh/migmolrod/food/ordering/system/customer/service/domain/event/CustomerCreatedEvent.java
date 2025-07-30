package ovh.migmolrod.food.ordering.system.customer.service.domain.event;

import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;

import java.time.ZonedDateTime;

public class CustomerCreatedEvent extends CustomerEvent {

	public CustomerCreatedEvent(Customer customer, ZonedDateTime createdAt) {
		super(customer, createdAt);
	}

}
