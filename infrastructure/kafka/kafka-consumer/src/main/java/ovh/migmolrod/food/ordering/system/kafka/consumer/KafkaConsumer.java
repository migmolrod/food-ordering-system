package ovh.migmolrod.food.ordering.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public interface KafkaConsumer<T extends SpecificRecordBase> {

	/**
	 * It is actually used in all listeners (adapters). But since they are used with `@KafkaListener`, they don't
	 * count as 'usages', apparently.
	 */
	@SuppressWarnings("unused")
	void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);

}
