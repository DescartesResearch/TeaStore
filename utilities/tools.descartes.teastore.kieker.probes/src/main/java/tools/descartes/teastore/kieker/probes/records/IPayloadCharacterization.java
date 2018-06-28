package tools.descartes.teastore.kieker.probes.records;

import kieker.common.record.IMonitoringRecord;

/**
 * Interface in order to realize parameter logging with Kieker.
 * 
 * @author Johannes Grohmann, Reiner Jung
 * 
 * 
 */
public interface IPayloadCharacterization extends IMonitoringRecord {
	public String[] getParameterTypes();

	public String[] getParameterValues();

	public String getReturnType();

	public String getReturnValue();

}