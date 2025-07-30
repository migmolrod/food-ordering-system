package ovh.migmolrod.food.ordering.system.order.service.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CustomerCreatedModel {

	private String id;
	private String username;
	private String firstName;
	private String lastName;

}
