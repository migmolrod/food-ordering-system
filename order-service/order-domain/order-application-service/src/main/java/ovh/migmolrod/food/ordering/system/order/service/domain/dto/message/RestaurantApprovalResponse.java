package ovh.migmolrod.food.ordering.system.order.service.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class RestaurantApprovalResponse {
	private String id;
	private String sagaId;
	private String orderId;
	private String restaurantId;
	private Instant createdAt;
	private OrderApprovalStatus orderApprovalStatus;
	private List<String> failureMessages;
}
