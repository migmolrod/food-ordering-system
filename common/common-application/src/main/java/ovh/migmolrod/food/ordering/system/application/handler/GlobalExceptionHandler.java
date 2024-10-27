package ovh.migmolrod.food.ordering.system.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ovh.migmolrod.food.ordering.system.application.ErrorDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorDto handleException(Exception exception) {
		log.error(exception.getMessage(), exception);
		return ErrorDto.builder()
				.code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("Internal Server Error")
				.build();
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDto handleException(ValidationException exception) {
		ErrorDto errorDto;

		if (exception instanceof ConstraintViolationException validationException) {
			String violations = extractViolationsFromException(validationException);
			log.error(violations, exception);
			errorDto = ErrorDto.builder()
					.code(HttpStatus.BAD_REQUEST.getReasonPhrase())
					.message(violations)
					.build();
		} else {
			log.error(exception.getMessage(), exception);
			errorDto = ErrorDto.builder()
					.code(HttpStatus.BAD_REQUEST.getReasonPhrase())
					.message(exception.getMessage())
					.build();
		}

		return errorDto;
	}

	private String extractViolationsFromException(ConstraintViolationException validationException) {
		return validationException.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining("--"));
	}

}
