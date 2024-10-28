package ovh.migmolrod.food.ordering.system.order.service.data.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.entity.RestaurantEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.restaurant.entity.RestaurantEntityId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {

	Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);

}
