package ovh.migmolrod.food.ordering.system.order.service.data.order.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ovh.migmolrod.food.ordering.system.domain.valueobject.*;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderAddressEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderEntity;
import ovh.migmolrod.food.ordering.system.order.service.data.order.entity.OrderItemEntity;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.OrderItem;
import ovh.migmolrod.food.ordering.system.order.service.domain.entity.Product;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

	public OrderEntity orderToOrderEntity(Order order) {
		OrderEntity orderEntity = OrderEntity.builder()
				.id(order.getId().getValue())
				.customerId(order.getCustomerId().getValue())
				.restaurantId(order.getRestaurantId().getValue())
				.trackingId(order.getTrackingId().getValue())
				.address(deliveryAddressToOrderAddressEntity(order.getDeliveryAddress()))
				.price(order.getPrice().getAmount())
				.orderItems(orderItemsToOrderItemEntities(order.getItems()))
				.orderStatus(order.getStatus())
				.failureMessages(order.getFailureMessages() != null ?
						String.join(Order.FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
				.build();
		orderEntity.getAddress().setOrder(orderEntity);
		orderEntity.getOrderItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

		return orderEntity;
	}

	public Order orderEntityToOrder(OrderEntity orderEntity) {
		return Order.builder()
				.orderId(new OrderId(orderEntity.getId()))
				.customerId(new CustomerId(orderEntity.getCustomerId()))
				.restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
				.deliveryAddress(orderAddressEntityToDeliveryAddress(orderEntity.getAddress()))
				.price(new Money(orderEntity.getPrice()))
				.items(orderItemEntitiesToOrderItems(orderEntity.getOrderItems()))
				.trackingId(new TrackingId(orderEntity.getTrackingId()))
				.status(orderEntity.getOrderStatus())
				.failureMessages(StringUtils.hasText(orderEntity.getFailureMessages()) ?
						new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(Order.FAILURE_MESSAGE_DELIMITER))) :
						new ArrayList<>())
				.build();
	}

	private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
		return items.stream().map(orderItem ->
				OrderItemEntity.builder()
						.id(orderItem.getId().getValue())
						.productId(orderItem.getProduct().getId().getValue())
						.quantity(orderItem.getQuantity())
						.price(orderItem.getPrice().getAmount())
						.subTotal(orderItem.getSubTotal().getAmount())
						.build()
		).collect(Collectors.toList());
	}

	private OrderAddressEntity deliveryAddressToOrderAddressEntity(StreetAddress deliveryAddress) {
		return OrderAddressEntity.builder()
				.id(deliveryAddress.getId())
				.street(deliveryAddress.getStreet())
				.postalCode(deliveryAddress.getPostalCode())
				.city(deliveryAddress.getCity())
				.build();
	}

	private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> orderItems) {
		return orderItems.stream().map(orderItemEntity ->
				OrderItem.builder()
						.orderItemId(new OrderItemId(orderItemEntity.getId()))
						.product(new Product(new ProductId(orderItemEntity.getProductId())))
						.price(new Money(orderItemEntity.getPrice()))
						.quantity(orderItemEntity.getQuantity())
						.subTotal(new Money(orderItemEntity.getSubTotal()))
						.build()
		).collect(Collectors.toList());
	}

	private StreetAddress orderAddressEntityToDeliveryAddress(OrderAddressEntity address) {
		return new StreetAddress(
				address.getId(),
				address.getStreet(),
				address.getPostalCode(),
				address.getCity()
		);
	}

}
