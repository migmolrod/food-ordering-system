package ovh.migmolrod.food.ordering.system.order.service.application.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

	private final OrderApplicationService service;

	public OrderController(OrderApplicationService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<CreateOrderResponse> createOrder(
			@RequestBody CreateOrderCommand createOrderCommand
	) {
		log.info("Creating order for customer {} at restaurant {}",
				createOrderCommand.getCustomerId(),
				createOrderCommand.getRestaurantId());

		CreateOrderResponse createOrderResponse = service.createOrder(createOrderCommand);
		log.info("Order created with tracking id {}", createOrderResponse.getOrderTrackingId());

		return ResponseEntity.ok(createOrderResponse);
	}

	@GetMapping(path = "/{trackingId}")
	public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(
			@PathVariable UUID trackingId
	) {
		TrackOrderQuery trackOrderQuery = TrackOrderQuery.builder().orderTrackingId(trackingId).build();

		TrackOrderResponse trackOrderResponse = service.trackOrder(trackOrderQuery);
		log.info("Returning order status with tracking id {}", trackOrderResponse.getOrderTrackingId());

		return ResponseEntity.ok(trackOrderResponse);
	}

}
