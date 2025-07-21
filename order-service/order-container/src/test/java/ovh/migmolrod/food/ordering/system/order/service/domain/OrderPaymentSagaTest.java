package ovh.migmolrod.food.ordering.system.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ovh.migmolrod.food.ordering.system.order.service.data.outbox.payment.repository.PaymentOutboxJpaRepository;
import ovh.migmolrod.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import ovh.migmolrod.food.ordering.system.order.service.domain.saga.OrderPaymentSaga;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = {OrderServiceApplication.class})
@Sql(value = {"classpath:sql/saga/01-set-up.sql"})
@Sql(value = {"classpath:sql/saga/99-clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderPaymentSagaTest {

	@Autowired
	private OrderPaymentSaga orderPaymentSaga;
	@Autowired
	private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

	private final UUID SAGA_ID = UUID.fromString("55da690a-f9b1-4b96-b079-58d352f0c545");
	private final UUID ORDER_ID = UUID.fromString("b9434aae-b91c-4b76-89a4-da6e48f3c185");
	private final UUID CUSTOMER_ID = UUID.fromString("fe1a2605-3471-46c5-9283-d283b4fa1477");
	private final UUID PAYMENT_ID = UUID.randomUUID();
	private final BigDecimal price = new BigDecimal("200");

	@Test
	void testDoublePayment() {
		orderPaymentSaga.process(getPaymentResponse());
		orderPaymentSaga.process(getPaymentResponse());

	}

	private PaymentResponse getPaymentResponse() {
		return PaymentResponse.builder()
				.id(UUID.randomUUID().toString())
				.sagaId(SAGA_ID.toString())
				.paymentStatus(ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus.COMPLETED)
				.paymentId(PAYMENT_ID.toString())
				.orderId(ORDER_ID.toString())
				.customerId(CUSTOMER_ID.toString())
				.price(price)
				.createdAt(Instant.now())
				.failureMessages(new ArrayList<>())
				.build();
	}

}
