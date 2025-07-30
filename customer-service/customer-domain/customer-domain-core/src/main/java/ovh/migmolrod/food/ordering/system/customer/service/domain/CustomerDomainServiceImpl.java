package ovh.migmolrod.food.ordering.system.customer.service.domain;

import lombok.extern.slf4j.Slf4j;
import ovh.migmolrod.food.ordering.system.customer.service.domain.entity.Customer;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;

@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {

	@Override
	public CustomerCreatedEvent validateAndInitializeCustomer(Customer customer) {
		log.info("Customer with id '{}' has been validated and initialized", customer.getId().getValue());

		return new CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)));
	}

}
