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
		PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);

		fireEvent(paymentEvent);
	}

	@Override
	public void cancelPayment(PaymentRequest paymentRequest) {
		PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);

		fireEvent(paymentEvent);
	}

	private void fireEvent(PaymentEvent paymentEvent) {
		log.info("Publishing payment event for payment {} and order id {}",
				paymentEvent.getPayment().getId().getValue(), paymentEvent.getPayment().getOrderId().getValue());

		paymentEvent.fire();
	}

}
