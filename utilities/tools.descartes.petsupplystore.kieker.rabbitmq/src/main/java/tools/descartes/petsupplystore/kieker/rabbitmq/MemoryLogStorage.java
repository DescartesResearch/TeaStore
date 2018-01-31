package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.util.LinkedList;
import java.util.List;

import kieker.common.record.IMonitoringRecord;

public class MemoryLogStorage {
	private static List<IMonitoringRecord> records = new LinkedList<IMonitoringRecord>();
	
	public static void storeRecord(IMonitoringRecord record) {
		records.add(record);
	}
	
	public static List<IMonitoringRecord> getRecords() {
		return records;
	}
	
	public static void clearMemoryStorage() {
		records.clear();
	}
}
