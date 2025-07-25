package ovh.migmolrod.food.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ovh.migmolrod.food.ordering.system.domain.valueobject.CustomerId;
import ovh.migmolrod.food.ordering.system.domain.valueobject.PaymentStatus;
import ovh.migmolrod.food.ordering.system.outbox.OutboxStatus;
import ovh.migmolrod.food.ordering.system.payment.service.domain.dto.message.PaymentRequest;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditEntry;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.CreditHistory;
import ovh.migmolrod.food.ordering.system.payment.service.domain.entity.Payment;
import ovh.migmolrod.food.ordering.system.payment.service.domain.event.PaymentEvent;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException;
import ovh.migmolrod.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import ovh.migmolrod.food.ordering.system.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import ovh.migmolrod.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
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
	private final OrderOutboxHelper orderOutboxHelper;
	private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

	public PaymentRequestHelper(
			PaymentDomainService paymentDomainService,
			PaymentDataMapper paymentDataMapper,
			PaymentRepository paymentRepository,
			CreditEntryRepository creditEntryRepository,
			CreditHistoryRepository creditHistoryRepository,
			OrderOutboxHelper orderOutboxHelper,
			PaymentResponseMessagePublisher paymentResponseMessagePublisher
	) {
		this.paymentDomainService = paymentDomainService;
		this.paymentDataMapper = paymentDataMapper;
		this.paymentRepository = paymentRepository;
		this.creditEntryRepository = creditEntryRepository;
		this.creditHistoryRepository = creditHistoryRepository;
		this.orderOutboxHelper = orderOutboxHelper;
		this.paymentResponseMessagePublisher = paymentResponseMessagePublisher;
	}

	@Transactional
	public void persistPayment(PaymentRequest paymentRequest) {
		if (this.publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
			log.info("An already completed outbox message with saga id '{}' is already saved to database",
					paymentRequest.getSagaId());
			return;
		}

		log.info("Received payment completion for order id {}", paymentRequest.getOrderId());

		Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
		CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
		List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
		List<String> failureMessages = new ArrayList<>();

		PaymentEvent paymentEvent = this.paymentDomainService.validateAndInitiatePayment(
				payment,
				creditEntry,
				creditHistories,
				failureMessages
		);
		persistDatabaseObjects(payment, failureMessages, creditEntry, creditHistories);

		orderOutboxHelper.saveOrderOutboxMessage(
				this.paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
				paymentEvent.getPayment().getPaymentStatus(),
				OutboxStatus.STARTED,
				UUID.fromString(paymentRequest.getSagaId())
		);
	}

	@Transactional
	public void persistCancelPayment(PaymentRequest paymentRequest) {
		if (this.publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
			log.info("An already cancelled outbox message with saga id '{}' is already saved to database",
					paymentRequest.getSagaId());
			return;
		}

		log.info("Received payment rollback for order id {}", paymentRequest.getOrderId());
		Optional<Payment> savedPayment = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

		if (savedPayment.isEmpty()) {
			String errorMessage = String.format("Could not find payment for order id %s", paymentRequest.getOrderId());
			log.error(errorMessage);
			throw new PaymentNotFoundException(errorMessage);
		}
		Payment payment = savedPayment.get();
		CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
		List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
		List<String> failureMessages = new ArrayList<>();

		PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
				payment,
				creditEntry,
				creditHistories,
				failureMessages
		);

		persistDatabaseObjects(payment, failureMessages, creditEntry, creditHistories);

		orderOutboxHelper.saveOrderOutboxMessage(
				this.paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
				paymentEvent.getPayment().getPaymentStatus(),
				OutboxStatus.STARTED,
				UUID.fromString(paymentRequest.getSagaId())
		);
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

	private boolean publishIfOutboxMessageProcessedForPayment(PaymentRequest request, PaymentStatus paymentStatus) {
		Optional<OrderOutboxMessage> orderOutboxMessageResult = orderOutboxHelper.getCompleted(
				UUID.fromString(request.getSagaId()),
				paymentStatus
		);

		if (orderOutboxMessageResult.isPresent()) {
			OrderOutboxMessage orderOutboxMessage = orderOutboxMessageResult.get();
			this.paymentResponseMessagePublisher.publish(orderOutboxMessage, this.orderOutboxHelper::updateOutboxStatus);
			return true;
		}

		return false;
	}

}
