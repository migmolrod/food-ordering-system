package ovh.migmolrod.food.ordering.system.restaurant.service.data.orderapproval.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_approvals")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderApprovalEntity {

	@Id
	private UUID id;
	private UUID restaurantId;
	private UUID orderId;
	@Enumerated(EnumType.STRING)
	private OrderApprovalStatus status;

}
