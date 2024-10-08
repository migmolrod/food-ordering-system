package ovh.migmolrod.food.ordering.system.order.service.domain.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;

import javax.validation.constraints.NotNull;
import java.rmi.server.UID;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class TrackOrderResponse {

	@NotNull
	private final UID orderTrackingId;
	@NotNull
	private final OrderStatus orderStatus;
	private final List<String> failureMessages;

}
