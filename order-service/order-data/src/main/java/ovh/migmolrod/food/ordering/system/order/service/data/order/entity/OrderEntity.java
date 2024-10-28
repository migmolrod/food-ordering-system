package ovh.migmolrod.food.ordering.system.order.service.data.order.entity;

import lombok.*;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

	@Id
	private UUID id;
	private UUID customerId;
	private UUID restaurantId;
	private UUID trackingId;
	private BigDecimal price;
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	private String failureMessages;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private OrderAddressEntity address;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItemEntity> orderItems;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderEntity that = (OrderEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
