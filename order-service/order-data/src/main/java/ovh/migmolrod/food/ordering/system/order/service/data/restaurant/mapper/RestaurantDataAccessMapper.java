package ovh.migmolrod.food.ordering.system.order.service.data.restaurant.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.data.restaurant.entity.RestaurantEntity;
import ovh.migmolrod.food.ordering.system.data.restaurant.exception.RestaurantDataAccessException;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.ProductId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

	public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
		return restaurant.getProducts().stream()
				.map(product -> product.getId().getValue())
				.toList();
	}

	public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
		RestaurantEntity restaurantEntity = restaurantEntities.stream()
				.findFirst()
				.orElseThrow(() -> new RestaurantDataAccessException("No restaurant found!"));

		List<Product> restaurantProducts = restaurantEntities.stream()
				.map(entity -> new Product(
						new ProductId(entity.getProductId()),
						entity.getProductName(),
						new Money(entity.getProductPrice())
				))
				.toList();

		return Restaurant.builder()
				.restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
				.products(restaurantProducts)
				.active(restaurantEntity.getRestaurantActive())
				.build();
	}

}
