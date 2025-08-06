package ovh.migmolrod.food.ordering.system.outbox;

public interface OutboxScheduler {

	/**
	 * It is actually used in all services with outbox schedulers. But since they are used with `@Scheduled`, they don't
	 * count as 'usages', apparently.
	 */
	@SuppressWarnings("unused")
	void processOutboxMessage();

}
