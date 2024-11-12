package ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.entity.OrderApprovalEntity;

import java.util.UUID;

@Repository
public interface OrderApprovalJapRepository extends JpaRepository<OrderApprovalEntity, UUID> {}
