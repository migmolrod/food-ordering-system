package ovh.migmolrod.food.ordering.system.restaurant.service.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {

	private String id;
	private String sagaId;
	private String restaurantId;
	private String orderId;
	private RestaurantOrderStatus restaurantOrderStatus;
	private List<Product> products;
	private BigDecimal price;
	private Instant createdAt;

}
