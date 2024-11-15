package ovh.migmolrod.food.ordering.system.payment.service.domain.mapper;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.Money;
import ovh.migmolrod.food.ordering.system.domain.valueobject.OrderId;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static ovh.migmolrod.food.ordering.system.domain.DomainConstants.DEFAULT_ZONE_ID;

@Component
public class PaymentDataMapper {

	public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
		return Payment.builder()
				.orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
				.customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
				.price(new Money(paymentRequest.getPrice()))
				.createdAt(ZonedDateTime.ofInstant(paymentRequest.getCreatedAt(), ZoneId.of(DEFAULT_ZONE_ID)))
				.build();
	}

}
