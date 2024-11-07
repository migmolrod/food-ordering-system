package ovh.migmolrod.food.ordering.system.payment.service.data.payment.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.payment.service.data.payment.entity.PaymentEntity;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.valueobject.PaymentId;

@Component
public class PaymentDataAccessMapper {

	public PaymentEntity paymentToPaymentEntity(Payment payment) {
		return PaymentEntity.builder()
				.id(payment.getId().getValue())
				.customerId(payment.getCustomerId().getValue())
				.orderId(payment.getOrderId().getValue())
				.price(payment.getPrice().getAmount())
				.createdAt(payment.getCreatedAt())
				.paymentStatus(payment.getPaymentStatus())
				.build();
	}

	public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
		return Payment.builder()
				.paymentId(new PaymentId(paymentEntity.getId()))
				.customerId(new CustomerId(paymentEntity.getCustomerId()))
				.orderId(new OrderId(paymentEntity.getOrderId()))
				.price(new Money(paymentEntity.getPrice()))
				.createdAt(paymentEntity.getCreatedAt())
				.paymentStatus(paymentEntity.getPaymentStatus())
				.build();
	}

}
