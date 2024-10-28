package ovh.migmolrod.food.ordering.system.order.service.data.order.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemEntityId.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntity {

	@Id
	private Long id;

	@Id
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id")
	private OrderEntity order;

	private UUID productId;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal subTotal;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderItemEntity that = (OrderItemEntity) o;
		return getId().equals(that.getId()) && getOrder().equals(that.getOrder());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, order);
	}

}
