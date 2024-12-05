package ovh.migmolrod.food.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

	private final PaymentRequestHelper paymentRequestHelper;

	public PaymentRequestMessageListenerImpl(
			PaymentRequestHelper paymentRequestHelper
	) {
		this.paymentRequestHelper = paymentRequestHelper;
	}

	@Override
	public void completePayment(PaymentRequest paymentRequest) {
		paymentRequestHelper.persistPayment(paymentRequest);
	}

	@Override
	public void cancelPayment(PaymentRequest paymentRequest) {
		paymentRequestHelper.persistCancelPayment(paymentRequest);
	}

}
