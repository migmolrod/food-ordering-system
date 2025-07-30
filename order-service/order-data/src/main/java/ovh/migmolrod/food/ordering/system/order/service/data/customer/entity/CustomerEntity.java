package ovh.migmolrod.food.ordering.system.order.service.data.customer.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {

	@Id
	private UUID id;
	private String username;
	private String firstName;
	private String lastName;

}
