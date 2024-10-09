package ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.service.message.listener.payment;

import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

	void paymentCompleted(PaymentResponse paymentResponse);

	void paymentCancelled(PaymentResponse paymentResponse);

}
