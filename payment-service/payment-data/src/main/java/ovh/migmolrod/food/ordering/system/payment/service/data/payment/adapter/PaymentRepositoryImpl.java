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

	private final PaymentJpaRepository paymentJpaRepository;
	private final PaymentDataAccessMapper paymentDataAccessMapper;

	public PaymentRepositoryImpl(
			PaymentJpaRepository paymentJpaRepository,
			PaymentDataAccessMapper paymentDataAccessMapper
	) {
		this.paymentJpaRepository = paymentJpaRepository;
		this.paymentDataAccessMapper = paymentDataAccessMapper;
	}

	@Override
	public Payment save(Payment payment) {
		PaymentEntity savedPaymentEntity = paymentJpaRepository.save(
				paymentDataAccessMapper.paymentToPaymentEntity(payment)
		);

		return paymentDataAccessMapper.paymentEntityToPayment(savedPaymentEntity);
	}

	@Override
	public Optional<Payment> findByOrderId(UUID orderId) {
		return paymentJpaRepository
				.findByOrderId(orderId)
				.map(paymentDataAccessMapper::paymentEntityToPayment);
	}

}
