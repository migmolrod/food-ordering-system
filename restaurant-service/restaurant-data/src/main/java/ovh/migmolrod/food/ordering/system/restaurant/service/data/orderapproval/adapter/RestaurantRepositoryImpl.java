package ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.data.restaurant.entity.RestaurantEntity;
import ovh.migmolrod.food.ordering.system.data.restaurant.repository.RestaurantJpaRepository;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.mapper.RestaurantDataAccessMapper;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;

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
		List<UUID> restaurantProducts = mapper.restaurantToRestaurantProducts(restaurant);

		Optional<List<RestaurantEntity>> restaurantEntities = jpaRepository.findByRestaurantIdAndProductIdIn(
				restaurant.getId().getValue(),
				restaurantProducts
		);
		return restaurantEntities.map(mapper::restaurantEntityToRestaurant);
	}

}
