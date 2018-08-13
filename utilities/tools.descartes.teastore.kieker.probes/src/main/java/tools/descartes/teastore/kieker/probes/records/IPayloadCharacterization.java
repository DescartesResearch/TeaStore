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
	
  /**
   * Getter for parameter types.
   * @return parameter types
   */
  public String[] getParameterTypes();

  /**
   * Getter for parameter values.
   * @return parameter values
   */
	public String[] getParameterValues();

	/**
	 * Getter for return type.
	 * @return return type
	 */
	public String getReturnType();

	/**
	 * Getter for return value.
	 * @return return value
	 */
	public String getReturnValue();

}