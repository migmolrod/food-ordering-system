package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import ovh.migmolrod.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;

import static ovh.migmolrod.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

	private final OrderPaymentSaga orderPaymentSaga;

	public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga) {
		this.orderPaymentSaga = orderPaymentSaga;
	}

	@Override
	public void paymentCompleted(PaymentResponse paymentResponse) {
		OrderPaidEvent event = orderPaymentSaga.process(paymentResponse);
		log.info("Publishing OrderPaidEvent for order id {}", paymentResponse.getOrderId());
		event.fire();
	}

	@Override
	public void paymentCancelled(PaymentResponse paymentResponse) {
		orderPaymentSaga.rollback(paymentResponse);
		log.info(
				"Rollback for order payment id {}. Failure messages: {}",
				paymentResponse.getOrderId(), String.join(FAILURE_MESSAGE_DELIMITER, paymentResponse.getFailureMessages())
		);
	}

}
