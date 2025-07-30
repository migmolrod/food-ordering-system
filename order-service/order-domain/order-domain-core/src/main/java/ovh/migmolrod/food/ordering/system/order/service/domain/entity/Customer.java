package ovh.migmolrod.food.ordering.system.order.service.domain.entity;

import ovh.migmolrod.food.ordering.system.domain.entity.AggregateRoot;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

	private String username;
	private String firstName;
	private String lastName;

	public Customer(CustomerId customerId) {
		super.setId(customerId);
	}

	public Customer(CustomerId customerId, String username, String firstName, String lastName) {
		this.setId(customerId);
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getUsername() {
		return this.username;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}
}
