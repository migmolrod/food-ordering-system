package ovh.migmolrod.food.ordering.system.payment.service.data.payment.adapter;

import org.springframework.stereotype.Component;
import ovh.migmolrod.food.ordering.system.payment.service.data.payment.entity.PaymentEntity;
import ovh.migmolrod.food.ordering.system.payment.service.data.payment.mapper.PaymentDataAccessMapper;
import ovh.migmolrod.food.ordering.system.payment.service.data.payment.repository.PaymentJpaRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

	private final PaymentJpaRepository jpaRepository;
	private final PaymentDataAccessMapper mapper;

	public PaymentRepositoryImpl(
			PaymentJpaRepository jpaRepository,
			PaymentDataAccessMapper mapper
	) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Payment save(Payment payment) {
		PaymentEntity savedPaymentEntity = jpaRepository.save(
				mapper.paymentToPaymentEntity(payment)
		);

		return mapper.paymentEntityToPayment(savedPaymentEntity);
	}

	@Override
	public Optional<Payment> findByOrderId(UUID orderId) {
		return jpaRepository
				.findByOrderId(orderId)
				.map(mapper::paymentEntityToPayment);
	}

}
