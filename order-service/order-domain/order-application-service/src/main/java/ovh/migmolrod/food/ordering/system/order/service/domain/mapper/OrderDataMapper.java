package ovh.migmolrod.food.ordering.system.order.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.ProductId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.OrderItem;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Restaurant;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.StreetAddress;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

	public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
		return Restaurant.builder()
				.restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
				.products(
						createOrderCommand.getItems()
								.stream()
								.map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
								.toList()
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

	public CreateOrderResponse orderToCreateOrderResponse(Order order) {
		return CreateOrderResponse.builder()
				.orderTrackingId(order.getTrackingId().getValue())
				.orderStatus(order.getStatus())
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
				).toList();
	}

}
