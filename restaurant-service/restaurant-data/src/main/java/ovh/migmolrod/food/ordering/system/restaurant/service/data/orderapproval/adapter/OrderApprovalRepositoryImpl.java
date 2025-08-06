package ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.mapper.RestaurantDataAccessMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.repository.OrderApprovalJapRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;

@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

	private final OrderApprovalJapRepository jpaRepository;
	private final RestaurantDataAccessMapper mapper;

	public OrderApprovalRepositoryImpl(
			OrderApprovalJapRepository jpaRepository,
			RestaurantDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public OrderApproval save(OrderApproval orderApproval) {
		return mapper.orderApprovalEntityToOrderApproval(
				jpaRepository.save(
						mapper.orderApprovalToOrderApprovalEntity(orderApproval)
				)
		);
	}

}
