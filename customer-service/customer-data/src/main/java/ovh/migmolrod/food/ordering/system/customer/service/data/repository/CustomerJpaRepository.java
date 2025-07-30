package ovh.migmolrod.food.ordering.system.customer.service.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ovh.migmolrod.food.ordering.system.customer.service.data.entity.CustomerEntity;

import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {}
