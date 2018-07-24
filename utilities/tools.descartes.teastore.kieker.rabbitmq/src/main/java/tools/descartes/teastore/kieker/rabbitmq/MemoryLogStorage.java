package tools.descartes.teastore.kieker.rabbitmq;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import kieker.common.record.IMonitoringRecord;

/**
 * Memory storage for kieker logs.
 * @author Simon
 *
 */
public final class MemoryLogStorage {
	private static Queue<IMonitoringRecord> records = new ConcurrentLinkedQueue<IMonitoringRecord>();

  /**
   * Hide default constructor.
   */
  private MemoryLogStorage() {

  }
  
  /**
   * Stores a new record.
   * @param record record that should be stored
   */
	public static void storeRecord(IMonitoringRecord record) {
		records.add(record);
	}
	
	/**
	 * Get current records.
	 * @return queue containing current records
	 */
	public static Queue<IMonitoringRecord> getRecords() {
		return records;
	}
	
	/**
	 * Clears memory storage.
	 */
	public static void clearMemoryStorage() {
		records.clear();
	}
}
