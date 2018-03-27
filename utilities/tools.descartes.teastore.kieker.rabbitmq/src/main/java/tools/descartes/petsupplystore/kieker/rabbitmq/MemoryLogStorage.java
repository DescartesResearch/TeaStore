package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import kieker.common.record.IMonitoringRecord;

public class MemoryLogStorage {
	private static Queue<IMonitoringRecord> records = new ConcurrentLinkedQueue<IMonitoringRecord>();
	
	public static void storeRecord(IMonitoringRecord record) {
		records.add(record);
	}
	
	public static Queue<IMonitoringRecord> getRecords() {
		return records;
	}
	
	public static void clearMemoryStorage() {
		records.clear();
	}
}
