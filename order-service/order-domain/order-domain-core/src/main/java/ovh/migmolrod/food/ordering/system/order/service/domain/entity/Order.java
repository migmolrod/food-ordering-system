package ovh.migmolrod.food.ordering.system.order.service.domain.entity;

import ovh.migmolrod.food.ordering.system.domain.entity.AggregateRoot;
import ovh.migmolrod.food.ordering.system.domain.valueobject.*;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import ovh.migmolrod.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

	private final CustomerId customerId;
	private final RestaurantId restaurantId;
	private final StreetAddress deliveryAddress;
	private final Money price;
	private final List<OrderItem> items;

	private TrackingId trackingId;
	private OrderStatus status;
	private List<String> failureMessages;

	private Order(Builder builder) {
		super.setId(builder.orderId);
		customerId = builder.customerId;
		restaurantId = builder.restaurantId;
		deliveryAddress = builder.deliveryAddress;
		price = builder.price;
		items = builder.items;
		trackingId = builder.trackingId;
		status = builder.status;
		failureMessages = builder.failureMessages;
	}

	public static Builder builder() {
		return new Builder();
	}

	public void initializeOrder() {
		setId(new OrderId(UUID.randomUUID()));
		trackingId = new TrackingId(UUID.randomUUID());
		status = OrderStatus.PENDING;
		failureMessages = new ArrayList<>();
		initializeOrderItems();
	}

	public void validateOrder() {
		validateInitialOrder();
		validateTotalPrice();
		validateItemsPrice();
	}

	private void validateInitialOrder() {
		if (status != null || getId() != null) {
			throw new OrderDomainException("Order is not in correct stat for initialization");
		}
	}

	private void validateTotalPrice() {
		if (price == null || !price.isGreaterThanZero()) {
			throw new OrderDomainException("Total price must be greater than zero");
		}
	}

	private void validateItemsPrice() {
		Money orderItemsTotal = items.stream().map(orderItem -> {
			validateItemPrice(orderItem);
			return orderItem.getSubTotal();
		}).reduce(Money.ZERO, Money::add);

		if (!price.equals(orderItemsTotal)) {
			throw new OrderDomainException(String.format("Total price (%s) does not match the sum of all items prices (%s)",
					price, orderItemsTotal));
		}
	}

	private void validateItemPrice(OrderItem orderItem) {
		if (!orderItem.isPriceValid()) {
			throw new OrderDomainException(String.format("Order item price (%s) is not valid for product %s",
					orderItem.getPrice().getAmount(), orderItem.getProduct().getId().getValue()));
		}
	}

	private void initializeOrderItems() {
		long itemId = 1;
		for (OrderItem item : items) {
			item.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
		}
	}

	public void pay() {
		if (status != OrderStatus.PENDING) {
			throw new OrderDomainException(String.format("Order %s is not in correct state for payment operation", getId()));
		}
		status = OrderStatus.PAID;
	}

	public void approve() {
		if (status != OrderStatus.PAID) {
			throw new OrderDomainException(String.format("Order %s is not in correct state for approval operation",
					getId()));
		}
		status = OrderStatus.APPROVED;
	}

	public void initCancel(List<String> failureMessages) {
		if (status != OrderStatus.PAID) {
			throw new OrderDomainException(String.format("Order %s is not in correct state for init cancel operation",
					getId()));
		}
		status = OrderStatus.CANCELLING;
		updateFailureMessages(failureMessages);
	}

	public void cancel(List<String> failureMessages) {
		if (status != OrderStatus.CANCELLING && status != OrderStatus.PENDING) {
			throw new OrderDomainException(String.format("Order %s is not in correct state for cancel operation", getId()));
		}
		status = OrderStatus.CANCELLED;
		updateFailureMessages(failureMessages);
	}

	private void updateFailureMessages(List<String> failureMessages) {
		if (this.failureMessages != null && failureMessages != null) {
			this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
		}
		if (this.failureMessages == null) {
			this.failureMessages = failureMessages;
		}
	}

	public CustomerId getCustomerId() {
		return customerId;
	}

	public RestaurantId getRestaurantId() {
		return restaurantId;
	}

	public StreetAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	public Money getPrice() {
		return price;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public TrackingId getTrackingId() {
		return trackingId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public List<String> getFailureMessages() {
		return failureMessages;
	}

	public static final class Builder {

		private OrderId orderId;
		private CustomerId customerId;
		private RestaurantId restaurantId;
		private StreetAddress deliveryAddress;
		private Money price;
		private List<OrderItem> items;
		private TrackingId trackingId;
		private OrderStatus status;
		private List<String> failureMessages;

		private Builder() {}

		public static Builder builder() {
			return new Builder();
		}

		public Builder id(OrderId val) {
			orderId = val;
			return this;
		}

		public Builder customerId(CustomerId val) {
			customerId = val;
			return this;
		}

		public Builder restaurantId(RestaurantId val) {
			restaurantId = val;
			return this;
		}

		public Builder deliveryAddress(StreetAddress val) {
			deliveryAddress = val;
			return this;
		}

		public Builder price(Money val) {
			price = val;
			return this;
		}

		public Builder items(List<OrderItem> val) {
			items = val;
			return this;
		}

		public Builder trackingId(TrackingId val) {
			trackingId = val;
			return this;
		}

		public Builder status(OrderStatus val) {
			status = val;
			return this;
		}

		public Builder failureMessages(List<String> val) {
			failureMessages = val;
			return this;
		}

		public Order build() {
			return new Order(this);
		}

	}

}
