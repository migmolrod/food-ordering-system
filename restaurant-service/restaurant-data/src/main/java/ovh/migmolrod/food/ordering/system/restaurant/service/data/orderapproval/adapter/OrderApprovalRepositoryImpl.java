package ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.mapper.RestaurantDataAccessMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.repository.OrderApprovalJapRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;

@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

	private final OrderApprovalJapRepository orderApprovalJapRepository;
	private final RestaurantDataAccessMapper restaurantDataAccessMapper;

	public OrderApprovalRepositoryImpl(
			OrderApprovalJapRepository orderApprovalJapRepository,
			RestaurantDataAccessMapper restaurantDataAccessMapper
	) {
		this.orderApprovalJapRepository = orderApprovalJapRepository;
		this.restaurantDataAccessMapper = restaurantDataAccessMapper;
	}

	@Override
	public OrderApproval save(OrderApproval orderApproval) {
		return restaurantDataAccessMapper.orderApprovalEntityToOrderApproval(
				orderApprovalJapRepository.save(
						restaurantDataAccessMapper.orderApprovalToOrderApprovalEntity(orderApproval)
				)
		);
	}

}
