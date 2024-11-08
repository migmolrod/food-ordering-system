package ovh.migmolrod.food.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditHistory;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

	private final PaymentDomainService paymentDomainService;
	private final PaymentDataMapper paymentDataMapper;
	private final PaymentRepository paymentRepository;
	private final CreditEntryRepository creditEntryRepository;
	private final CreditHistoryRepository creditHistoryRepository;
	private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
	private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
	private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;

	public PaymentRequestHelper(
			PaymentDomainService paymentDomainService,
			PaymentDataMapper paymentDataMapper,
			PaymentRepository paymentRepository,
			CreditEntryRepository creditEntryRepository,
			CreditHistoryRepository creditHistoryRepository,
			PaymentCompletedMessagePublisher paymentCompletedMessagePublisher,
			PaymentCancelledMessagePublisher paymentCancelledMessagePublisher,
			PaymentFailedMessagePublisher paymentFailedMessagePublisher
	) {
		this.paymentDomainService = paymentDomainService;
		this.paymentDataMapper = paymentDataMapper;
		this.paymentRepository = paymentRepository;
		this.creditEntryRepository = creditEntryRepository;
		this.creditHistoryRepository = creditHistoryRepository;
		this.paymentCompletedMessagePublisher = paymentCompletedMessagePublisher;
		this.paymentCancelledMessagePublisher = paymentCancelledMessagePublisher;
		this.paymentFailedMessagePublisher = paymentFailedMessagePublisher;
	}

	@Transactional
	public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
		log.info("Received payment completion for order id {}", paymentRequest.getOrderId());

		Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
		CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
		List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
		List<String> failureMessages = new ArrayList<>();

		PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(
				payment,
				creditEntry,
				creditHistories,
				failureMessages,
				paymentCompletedMessagePublisher,
				paymentFailedMessagePublisher
		);
		persistDatabaseObjects(payment, failureMessages, creditEntry, creditHistories);

		return paymentEvent;
	}

	@Transactional
	public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
		log.info("Received payment rollback for order id {}", paymentRequest.getOrderId());
		Optional<Payment> savedPayment = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

		if (savedPayment.isEmpty()) {
			String errorMessage = String.format("Could not find payment for order id %s", paymentRequest.getOrderId());
			log.error(errorMessage);
			throw new PaymentApplicationServiceException(errorMessage);
		}
		Payment payment = savedPayment.get();
		CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
		List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
		List<String> failureMessages = new ArrayList<>();

		PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
				payment,
				creditEntry,
				creditHistories,
				failureMessages,
				paymentCancelledMessagePublisher,
				paymentFailedMessagePublisher
		);

		persistDatabaseObjects(payment, failureMessages, creditEntry, creditHistories);

		return paymentEvent;
	}

	private CreditEntry getCreditEntry(CustomerId customerId) {
		Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);

		if (creditEntry.isEmpty()) {
			String errorMessage = String.format("Could not find credit entry for customer id %s", customerId);
			log.error(errorMessage);
			throw new PaymentApplicationServiceException(errorMessage);
		}

		return creditEntry.get();
	}

	private List<CreditHistory> getCreditHistories(CustomerId customerId) {
		Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);

		if (creditHistories.isEmpty()) {
			String errorMessage = String.format("Could not find credit history for customer %s", customerId);
			log.error(errorMessage);
			throw new PaymentApplicationServiceException(errorMessage);
		}

		return creditHistories.get();
	}

	private void persistDatabaseObjects(
			Payment payment, List<String> failureMessages, CreditEntry creditEntry, List<CreditHistory> creditHistories
	) {
		paymentRepository.save(payment);

		if (failureMessages.isEmpty()) {
			creditEntryRepository.save(creditEntry);
			creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
		}
	}

}
