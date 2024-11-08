package ovh.migmolrod.food.ordering.system.order.service.data.restaurant.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_restaurant_m_view", schema = "restaurant")
@IdClass(RestaurantEntityId.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntity {

	@Id
	private UUID restaurantId;

	@Id
	private UUID productId;

	private String restaurantName;
	private Boolean restaurantActive;
	private String productName;
	private BigDecimal productPrice;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RestaurantEntity that = (RestaurantEntity) o;
		return getRestaurantId().equals(that.getRestaurantId()) && getProductId().equals(that.getProductId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(restaurantId, productId);
	}

}
