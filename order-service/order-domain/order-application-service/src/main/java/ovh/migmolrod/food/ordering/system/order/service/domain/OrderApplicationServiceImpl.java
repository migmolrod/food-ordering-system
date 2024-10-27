package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;

@Slf4j
@Service
@Validated
class OrderApplicationServiceImpl implements OrderApplicationService {

	private final OrderCreateCommandHandler orderCreateCommandHandler;
	private final OrderTrackCommandHandler orderTrackCommandHandler;

	OrderApplicationServiceImpl(
			OrderCreateCommandHandler orderCreateCommandHandler,
			OrderTrackCommandHandler orderTrackCommandHandler
	) {
		this.orderCreateCommandHandler = orderCreateCommandHandler;
		this.orderTrackCommandHandler = orderTrackCommandHandler;
	}

	@Override
	public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
		return this.orderCreateCommandHandler.createOrder(createOrderCommand);
	}

	@Override
	public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
		return this.orderTrackCommandHandler.trackOrder(trackOrderQuery);
	}

}
