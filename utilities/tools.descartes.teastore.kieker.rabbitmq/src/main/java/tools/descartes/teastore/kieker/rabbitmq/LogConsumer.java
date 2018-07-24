package tools.descartes.teastore.kieker.rabbitmq;

import kieker.analysis.IProjectContext;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.system.CPUUtilizationRecord;

/**
 * Consumes the logs from the rabbitmq.
 * @author Simon
 *
 */
public class LogConsumer extends AbstractFilterPlugin {

  /**
   * Name of kieker input port used.
   */
	public static final String INPUT_PORT_NAME = "newMonitoringRecord";

	/**
	 * Constructor that delegates to AbstractFilterPlugin.
	 * @param configuration configuration
	 * @param projectContext copntext
	 */
	public LogConsumer(final Configuration configuration, final IProjectContext projectContext) {
		super(configuration, projectContext);
	}
	
	/**
	 * process new monitoring record.
	 * @param record record.
	 */
	@InputPort(name = LogConsumer.INPUT_PORT_NAME, eventTypes = { IMonitoringRecord.class })
	public void newMonitoringRecord(final Object record) {
		if (record instanceof IMonitoringRecord) {
			if (record instanceof CPUUtilizationRecord) {
				CPUUtilizationRecord cpu = (CPUUtilizationRecord) record;
				System.out.println(cpu.getHostname() + cpu.getTotalUtilization());
			}
			IMonitoringRecord monitoringRecord = (IMonitoringRecord) record;
			MemoryLogStorage.storeRecord(monitoringRecord);
		} else {
			throw new IllegalStateException("Unknown monitoring result type");
		}
	}

	/**
	 * Getter for configuration.
	 * @return configuration
	 */
	@Override
	public Configuration getCurrentConfiguration() {
		return new Configuration();
	}

}
