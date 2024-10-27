package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

	@Override
	public void paymentCompleted(PaymentResponse paymentResponse) {
		// TODO in saga pattern implementation
	}

	@Override
	public void paymentCancelled(PaymentResponse paymentResponse) {
		// TODO in saga pattern implementation
	}

}
