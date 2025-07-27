package ovh.migmolrod.food.ordering.system.saga;

public enum SagaStatus {
	STARTED,
	FAILED,
	SUCCEEDED,
	PROCESSING,
	COMPENSATING,
	COMPENSATED
}
