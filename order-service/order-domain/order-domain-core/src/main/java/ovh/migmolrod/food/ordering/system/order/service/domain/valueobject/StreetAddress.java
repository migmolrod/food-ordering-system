package ovh.migmolrod.food.ordering.system.order.service.domain.valueobject;

import java.util.UUID;

public class StreetAddress {

	private final UUID id;
	private final String street;
	private final String postalCode;
	private final String city;

	public StreetAddress(UUID id, String street, String postalCode, String city) {
		this.id = id;
		this.street = street;
		this.postalCode = postalCode;
		this.city = city;
	}

	public UUID getId() {
		return id;
	}

	public String getStreet() {
		return street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StreetAddress that = (StreetAddress) o;
		return getStreet().equals(that.getStreet()) && getPostalCode().equals(that.getPostalCode()) && getCity().equals(that.getCity());
	}

	@Override
	public int hashCode() {
		int result = getStreet().hashCode();
		result = 31 * result + getPostalCode().hashCode();
		result = 31 * result + getCity().hashCode();
		return result;
	}

}
