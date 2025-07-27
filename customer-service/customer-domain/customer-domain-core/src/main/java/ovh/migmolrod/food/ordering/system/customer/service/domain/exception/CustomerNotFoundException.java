package ovh.migmolrod.food.ordering.system.customer.service.domain.exception;

import ovh.migmolrod.food.ordering.system.domain.exception.DomainException;

public class CustomerNotFoundException extends DomainException {

	public CustomerNotFoundException(String message) {
		super(message);
	}

	public CustomerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
