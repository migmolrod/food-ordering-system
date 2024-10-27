package ovh.migmolrod.food.ordering.system.order.service.application.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ovh.migmolrod.food.ordering.system.order.service.application.exception.ErrorDto;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderDomainException;
import ovh.migmolrod.food.ordering.system.order.service.domain.exception.OrderNotFoundException;

@Slf4j
@RestControllerAdvice
public class OrderGlobalExceptionHandler {

	@ExceptionHandler(OrderDomainException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleException(OrderDomainException orderDomainException) {
		log.error(orderDomainException.getMessage(), orderDomainException);
		return ErrorDto.builder()
				.code(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(orderDomainException.getMessage())
				.build();
	}

	@ExceptionHandler(OrderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDto handleException(OrderNotFoundException orderNotFoundException) {
		log.error(orderNotFoundException.getMessage(), orderNotFoundException);
		return ErrorDto.builder()
				.code(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(orderNotFoundException.getMessage())
				.build();
	}

}
