package ovh.migmolrod.food.ordering.system.outbox;

public interface OutboxScheduler {

	void processOutboxMessage();

}
