package ovh.migmolrod.food.ordering.system.order.service.data.restaurant.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.data.restaurant.entity.RestaurantEntity;
import ovh.migmolrod.food.ordering.system.data.restaurant.repository.RestaurantJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.mapper.RestaurantDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

	private final RestaurantJpaRepository jpaRepository;
	private final RestaurantDataAccessMapper mapper;

	public RestaurantRepositoryImpl(
			RestaurantJpaRepository jpaRepository,
			RestaurantDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
		List<UUID> restaurantProductIds = mapper.restaurantToRestaurantProducts(restaurant);

		Optional<List<RestaurantEntity>> restaurantEntities = jpaRepository.findByRestaurantIdAndProductIdIn(
				restaurant.getId().getValue(),
				restaurantProductIds
		);

		return restaurantEntities.map(mapper::restaurantEntityToRestaurant);
	}

}
