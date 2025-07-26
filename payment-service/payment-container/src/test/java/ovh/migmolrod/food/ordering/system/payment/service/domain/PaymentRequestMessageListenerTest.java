package ovh.migmolrod.food.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.data.outbox.entity.OrderOutboxEntity;
import ovh.migmolrod.food.ordering.system.payment.service.data.outbox.repository.OrderOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ovh.migmolrod.food.ordering.system.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@SpringBootTest(classes = {PaymentServiceApplication.class})
public class PaymentRequestMessageListenerTest {

	@Autowired
	private PaymentRequestMessageListenerImpl paymentRequestMessageListener;

	@Autowired
	private OrderOutboxJpaRepository orderOutboxJpaRepository;

	private final static String CUSTOMER_ID = "d215b5f8-0249-4dc5-89a3-51fd148cfb55";
	private final static BigDecimal PRICE = new BigDecimal("100");

	@Test
	void testDoublePayment() {
		String sagaId = UUID.randomUUID().toString();
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			this.paymentRequestMessageListener.completePayment(this.getPaymentRequest(sagaId));
			this.paymentRequestMessageListener.completePayment(this.getPaymentRequest(sagaId));
		});
		this.assertOrderOutbox(sagaId);
	}

	@Test
	void testDoublePaymentWithThreads() {
		String sagaId = UUID.randomUUID().toString();
		ExecutorService executorService = null;

		try {
			executorService = Executors.newFixedThreadPool(2);
			List<Callable<Object>> tasks = new ArrayList<>();

			tasks.add(Executors.callable(() -> {
				try {
					paymentRequestMessageListener.completePayment(this.getPaymentRequest(sagaId));
				} catch (Exception e) {
					log.error("Error calling the payment process in Thread-1. Error: {}", e.getMessage(), e);
				}
			}));
			tasks.add(Executors.callable(() -> {
				try {
					paymentRequestMessageListener.completePayment(this.getPaymentRequest(sagaId));
				} catch (Exception e) {
					log.error("Error calling the payment process in Thread-2. Error: {}", e.getMessage(), e);
				}
			}));

			ExecutorService finalExecutorService = executorService;
			finalExecutorService.invokeAll(tasks);

			this.assertOrderOutbox(sagaId);
		} catch (InterruptedException e) {
			log.error("Error calling the payment process. Error: {}", e.getMessage(), e);
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
		}
	}

	private PaymentRequest getPaymentRequest(String sagaId) {
		return PaymentRequest.builder()
				.id(UUID.randomUUID().toString())
				.sagaId(sagaId)
				.orderId(UUID.randomUUID().toString())
				.customerId(CUSTOMER_ID)
				.price(PRICE)
				.createdAt(Instant.now())
				.paymentOrderStatus(PaymentOrderStatus.PENDING)
				.build();
	}

	private void assertOrderOutbox(String sagaId) {
		Optional<OrderOutboxEntity> outboxEntity =
				this.orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
						ORDER_SAGA_NAME,
						UUID.fromString(sagaId),
						PaymentStatus.COMPLETED,
						OutboxStatus.STARTED
				);
		Assertions.assertTrue(outboxEntity.isPresent());
		Assertions.assertEquals(sagaId, outboxEntity.get().getSagaId().toString());
	}

}
