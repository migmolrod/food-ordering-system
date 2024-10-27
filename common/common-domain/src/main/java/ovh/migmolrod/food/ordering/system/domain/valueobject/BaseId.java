package ovh.migmolrod.food.ordering.system.domain.valueobject;

public abstract class BaseId<T> {

	private final T value;

	protected BaseId(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseId<?> baseId = (BaseId<?>) o;
		return getValue().equals(baseId.getValue());
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

}
