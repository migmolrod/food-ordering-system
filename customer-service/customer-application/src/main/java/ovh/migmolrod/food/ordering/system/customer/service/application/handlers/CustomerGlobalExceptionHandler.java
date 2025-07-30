package ovh.migmolrod.food.ordering.system.customer.service.application.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ovh.migmolrod.food.ordering.system.application.ErrorDto;
import ovh.migmolrod.food.ordering.system.application.exception.handler.GlobalExceptionHandler;
import ovh.migmolrod.food.ordering.system.customer.service.domain.exception.CustomerDomainException;

@Slf4j
@RestControllerAdvice
public class CustomerGlobalExceptionHandler extends GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(CustomerDomainException.class)
	public ErrorDto handleException(CustomerDomainException exception) {
		log.error(exception.getMessage(), exception);
		return ErrorDto.builder()
				.code(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(exception.getMessage())
				.build();
	}

}
