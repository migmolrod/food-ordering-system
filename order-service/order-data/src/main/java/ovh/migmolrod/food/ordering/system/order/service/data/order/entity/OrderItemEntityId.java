package ovh.migmolrod.food.ordering.system.order.service.data.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntityId implements Serializable {

	private Long id;
	private OrderEntity order;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderItemEntityId that = (OrderItemEntityId) o;
		return getId().equals(that.getId()) && getOrder().equals(that.getOrder());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, order);
	}

}
