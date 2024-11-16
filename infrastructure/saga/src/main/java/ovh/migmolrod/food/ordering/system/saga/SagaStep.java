package ovh.migmolrod.food.ordering.system.saga;

import ovh.migmolrod.food.ordering.system.domain.event.DomainEvent;

public interface SagaStep<T, S extends DomainEvent<?>, U extends DomainEvent<?>> {

	S process(T data);

	S rollback(T data);

}
