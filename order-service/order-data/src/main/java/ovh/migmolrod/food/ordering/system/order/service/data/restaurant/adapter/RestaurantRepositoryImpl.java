package ovh.migmolrod.food.ordering.system.order.service.data.restaurant.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.entity.RestaurantEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.mapper.RestaurantDataAccessMapper;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.repository.RestaurantJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

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
		List<UUID> restaurantProductIds = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

		Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
				restaurant.getId().getValue(),
				restaurantProductIds
		);

		return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
	}

}
