package ovh.migmolrod.food.ordering.system.customer.service.messaging.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import ovh.migmolrod.food.ordering.system.kafka.customer.avro.model.CustomerAvroModel;

import java.util.UUID;

@Component
public class CustomerMessagingMapper {

	public CustomerAvroModel eventToAvroModel(CustomerCreatedEvent event) {
		return CustomerAvroModel.newBuilder()
				.setId(event.getCustomer().getId().getValue().toString())
				.setUsername(event.getCustomer().getUsername())
				.setFirstName(event.getCustomer().getFirstName())
				.setLastName(event.getCustomer().getLastName())
				.build();
	}

}
