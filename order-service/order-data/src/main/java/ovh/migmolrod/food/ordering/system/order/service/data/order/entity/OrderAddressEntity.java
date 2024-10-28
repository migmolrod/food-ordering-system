package ovh.migmolrod.food.ordering.system.order.service.data.order.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_addresses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddressEntity {

	@Id
	private UUID id;
	private String street;
	private String postalCode;
	private String city;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id")
	private OrderEntity order;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OrderAddressEntity that = (OrderAddressEntity) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
