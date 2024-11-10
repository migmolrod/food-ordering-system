package ovh.migmolrod.food.ordering.system.restaurant.service.domain.entity;

import ovh.migmolrod.food.ordering.system.domain.entity.AggregateRoot;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.RestaurantId;
import ovh.migmolrod.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {

	private final OrderDetail orderDetail;
	private OrderApproval orderApproval;
	private boolean active;

	private Restaurant(Builder builder) {
		setId(builder.restaurantId);
		orderApproval = builder.orderApproval;
		active = builder.active;
		orderDetail = builder.orderDetail;
	}

	public static Builder builder() {
		return new Builder();
	}

	public void validateOrder(List<String> failureMessages) {
		if (!OrderStatus.PAID.equals(orderDetail.getOrderStatus())) {
			failureMessages.add(
					String.format("Payment is not completed for order %s", orderDetail.getId().getValue())
			);
		}
		Money totalAmount = orderDetail.getProducts().stream().map(product -> {
			if (!product.isAvailable()) {
				failureMessages.add(String.format("Product %s is not available", product.getName()));
			}
			return product.getPrice().multiply(product.getQuantity());
		}).reduce(Money.ZERO, Money::add);

		if (!totalAmount.equals(orderDetail.getTotalAmount())) {
			failureMessages.add(String.format(
					"Price total %s is not correct for order %s",
					totalAmount.getAmount(), orderDetail.getId().getValue()
			));
		}
	}

	public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
		this.orderApproval = OrderApproval.builder()
				.orderApprovalId(new OrderApprovalId(UUID.randomUUID()))
				.restaurantId(getId())
				.orderId(orderDetail.getId())
				.approvalStatus(orderApprovalStatus)
				.build();
	}

	public OrderApproval getOrderApproval() {
		return orderApproval;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public OrderDetail getOrderDetail() {
		return orderDetail;
	}

	public static final class Builder {

		private RestaurantId restaurantId;
		private OrderApproval orderApproval;
		private boolean active;
		private OrderDetail orderDetail;

		private Builder() {}

		public Builder restaurantId(RestaurantId val) {
			restaurantId = val;
			return this;
		}

		public Builder orderApproval(OrderApproval val) {
			orderApproval = val;
			return this;
		}

		public Builder active(boolean val) {
			active = val;
			return this;
		}

		public Builder orderDetail(OrderDetail val) {
			orderDetail = val;
			return this;
		}

		public Restaurant build() {
			return new Restaurant(this);
		}

	}

}
