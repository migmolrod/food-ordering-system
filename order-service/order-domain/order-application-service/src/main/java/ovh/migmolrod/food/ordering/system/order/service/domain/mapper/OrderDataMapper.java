package ovh.migmolrod.food.ordering.system.order.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.OrderItem;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import ovh.migmolrod.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.StreetAddress;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

	public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
		return Restaurant.builder()
				.restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
				.products(
						createOrderCommand.getItems()
								.stream()
								.map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
								.collect(Collectors.toList())
				)
				.build();
	}

	public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
		return Order.builder()
				.customerId(new CustomerId(createOrderCommand.getCustomerId()))
				.restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
				.deliveryAddress(this.orderAddressToStreetAddress(createOrderCommand.getAddress()))
				.price(new Money(createOrderCommand.getPrice()))
				.items(this.orderItemsToOrderItemEntities(createOrderCommand.getItems()))
				.build();
	}

	public TrackOrderResponse orderToTrackResponse(Order order) {
		return TrackOrderResponse.builder()
				.orderTrackingId(order.getTrackingId().getValue())
				.orderStatus(order.getStatus())
				.build();
	}

	public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
		return CreateOrderResponse.builder()
				.orderTrackingId(order.getTrackingId().getValue())
				.orderStatus(order.getStatus())
				.message(message)
				.build();
	}

	public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent event) {
		return OrderPaymentEventPayload.builder()
				.orderId(event.getOrder().getId().getValue().toString())
				.customerId(event.getOrder().getCustomerId().getValue().toString())
				.price(event.getOrder().getPrice().getAmount())
				.createdAt(event.getCreatedAt())
				.paymentOrderStatus(PaymentOrderStatus.PENDING.name())
				.build();
	}

	public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent event) {
		return OrderPaymentEventPayload.builder()
				.orderId(event.getOrder().getId().getValue().toString())
				.customerId(event.getOrder().getCustomerId().getValue().toString())
				.price(event.getOrder().getPrice().getAmount())
				.createdAt(event.getCreatedAt())
				.paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
				.build();
	}

	public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent event) {
		return OrderApprovalEventPayload.builder()
				.orderId(event.getOrder().getId().getValue().toString())
				.restaurantId(event.getOrder().getRestaurantId().getValue().toString())
				.restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
				.products(event.getOrder().getItems().stream()
						.map(item ->
								OrderApprovalEventProduct.builder()
										.id(item.getId().getValue().toString())
										.quantity(item.getQuantity())
										.build()
						)
						.collect(Collectors.toList()))
				.price(event.getOrder().getPrice().getAmount())
				.createdAt(event.getCreatedAt())
				.build();
	}

	private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
		return new StreetAddress(
				UUID.randomUUID(),
				orderAddress.getStreet(),
				orderAddress.getPostalCode(),
				orderAddress.getCity()
		);
	}

	private List<OrderItem> orderItemsToOrderItemEntities(
			List<ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems
	) {
		return orderItems.stream()
				.map(orderItem ->
						OrderItem.builder()
								.product(new Product(new ProductId(orderItem.getProductId())))
								.price(new Money(orderItem.getPrice()))
								.quantity(orderItem.getQuantity())
								.subTotal(new Money(orderItem.getSubTotal()))
								.build()
				).collect(Collectors.toList());
	}

}
