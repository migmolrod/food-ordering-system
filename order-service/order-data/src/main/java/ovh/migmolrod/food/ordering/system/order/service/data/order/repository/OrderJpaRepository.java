package ovh.migmolrod.food.ordering.system.order.service.data.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

	Optional<OrderEntity> findByTrackingId(UUID trackingId);

}
