package ovh.migmolrod.food.ordering.system.customer.service.data.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	private UUID id;
	private String username;
	private String firstName;
	private String lastName;

}
