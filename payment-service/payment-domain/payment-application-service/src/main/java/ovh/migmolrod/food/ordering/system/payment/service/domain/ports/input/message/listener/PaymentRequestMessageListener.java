package ovh.migmolrod.food.ordering.system.payment.service.domain.ports.input.message.listener;

import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;

public interface PaymentRequestMessageListener {

	void completePayment(PaymentRequest paymentRequest);

	void cancelPayment(PaymentRequest paymentRequest);

}
