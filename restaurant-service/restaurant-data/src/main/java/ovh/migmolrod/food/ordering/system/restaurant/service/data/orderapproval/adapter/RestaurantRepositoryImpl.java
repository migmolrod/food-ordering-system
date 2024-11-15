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

	private final RestaurantJpaRepository restaurantJpaRepository;
	private final RestaurantDataAccessMapper restaurantDataAccessMapper;

	public RestaurantRepositoryImpl(
			RestaurantJpaRepository restaurantJpaRepository,
			RestaurantDataAccessMapper restaurantDataAccessMapper
	) {
		this.restaurantJpaRepository = restaurantJpaRepository;
		this.restaurantDataAccessMapper = restaurantDataAccessMapper;
	}

	@Override
	public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
		List<UUID> restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

		Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
				restaurant.getId().getValue(),
				restaurantProducts
		);
		return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
	}

}
