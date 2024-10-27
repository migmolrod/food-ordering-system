package ovh.migmolrod.food.ordering.system.order.service.application.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorDto {

	private final String code;
	private final String message;

}
