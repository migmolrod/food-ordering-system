package ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.customer;

import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.CustomerCreatedModel;

public interface CustomerCreatedMessageListener {

	void customerCreated(CustomerCreatedModel customerCreatedModel);

}
