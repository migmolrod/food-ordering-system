package ovh.migmolrod.food.ordering.system.order.service.domain.helper.track;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

@Slf4j
@Component
public class OrderTrackCommandHandler {

	private final OrderTrackHelper orderTrackHelper;
	private final OrderDataMapper orderDataMapper;

	public OrderTrackCommandHandler(
			OrderTrackHelper orderTrackHelper,
			OrderDataMapper orderDataMapper
	) {
		this.orderTrackHelper = orderTrackHelper;
		this.orderDataMapper = orderDataMapper;
	}

	public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
		Order order = this.orderTrackHelper.trackOrder(new TrackingId(trackOrderQuery.getOrderTrackingId()));

		return orderDataMapper.orderToTrackResponse(order);
	}

}
