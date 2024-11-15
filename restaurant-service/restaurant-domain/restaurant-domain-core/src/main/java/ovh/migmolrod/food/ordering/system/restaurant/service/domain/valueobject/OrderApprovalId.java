package ovh.migmolrod.food.ordering.system.restaurant.service.domain.valueobject;

import ovh.migmolrod.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {

	public OrderApprovalId(UUID value) {
		super(value);
	}

}
