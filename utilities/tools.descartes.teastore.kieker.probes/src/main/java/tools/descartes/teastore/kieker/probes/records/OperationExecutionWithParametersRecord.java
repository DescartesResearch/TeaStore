package tools.descartes.teastore.kieker.probes.records;

import java.nio.BufferOverflowException;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

/**
 * Class for storing the Monitored execution recrods with parameters.
 * 
 * @author Johannes Grohmann
 * 
 *         API compatibility: Kieker 1.13.0
 * 
 */
public class OperationExecutionWithParametersRecord extends OperationExecutionRecord
    implements IPayloadCharacterization {

  private static final long serialVersionUID = 5027368663979062260L;

  /** Descriptive definition of the serialization size of the record. */
  public static final int SIZE = TYPE_SIZE_STRING // OperationExecutionRecord.operationSignature
      + TYPE_SIZE_STRING // OperationExecutionRecord.sessionId
      + TYPE_SIZE_LONG // OperationExecutionRecord.traceId
      + TYPE_SIZE_LONG // OperationExecutionRecord.tin
      + TYPE_SIZE_LONG // OperationExecutionRecord.tout
      + TYPE_SIZE_STRING // OperationExecutionRecord.hostname
      + TYPE_SIZE_INT // OperationExecutionRecord.eoi
      + TYPE_SIZE_INT // OperationExecutionRecord.ess
      + TYPE_SIZE_STRING // IPayloadCharacterization.parameterTypes
      + TYPE_SIZE_STRING // IPayloadCharacterization.parameterValues
      + TYPE_SIZE_STRING // IPayloadCharacterization.returnType
      + TYPE_SIZE_STRING; // IPayloadCharacterization.returnVal

  public static final Class<?>[] TYPES = { String.class, // OperationExecutionRecord.operationSignature
      String.class, // OperationExecutionRecord.sessionId
      long.class, // OperationExecutionRecord.traceId
      long.class, // OperationExecutionRecord.tin
      long.class, // OperationExecutionRecord.tout
      String.class, // OperationExecutionRecord.hostname
      int.class, // OperationExecutionRecord.eoi
      int.class, // OperationExecutionRecord.ess
      String[].class, // IPayloadCharacterization.parameterTypes
      String[].class, // IPayloadCharacterization.parameterValues
      String.class, // IPayloadCharacterization.returnType
      String.class, // IPayloadCharacterization.returnVal
  };

  /** user-defined constants. */
  public static final String[] NO_PARAMETERS = {};
  public static final String NO_RETURN = "<no-return-type>";

  /** default constants. */
  public static final String[] PARAMETER_TYPES = NO_PARAMETERS;
  public static final String[] PARAMETER_VALUES = NO_PARAMETERS;
  public static final String RETURN_TYPE = NO_RETURN;
  public static final String RETURN_VALUE = NO_RETURN;

  /** property name array. */
  private static final String[] PROPERTY_NAMES = { "operationSignature", "sessionId", "traceId",
      "tin", "tout", "hostname", "eoi", "ess", "parameterTypes", "parameterValues", "returnType",
      "returnValue", };

  /** property declarations. */
  private final String[] parameterTypes;
  private final String[] parameterValues;
  private final String returnType;
  private final String returnValue;

  /**
   * Creates a new instance of this class using the given parameters.
   * 
   * @param operationSignature
   * @param sessionId
   * @param traceId
   * @param tin
   * @param tout
   * @param hostname
   * @param eoi
   * @param ess
   * @param parameterTypes
   * @param parameterValues
   * @param returnType
   * @param returnValue
   */
  public OperationExecutionWithParametersRecord(final String operationSignature,
      final String sessionId, final long traceId, final long tin, final long tout,
      final String hostname, final int eoi, final int ess, final String[] parameterTypes,
      final String[] parameterValues, final String returnType, final String returnValue) {
    super(operationSignature, sessionId, traceId, tin, tout, hostname, eoi, ess);
    this.parameterTypes = parameterTypes;
    this.parameterValues = parameterValues;
    this.returnType = returnType;
    this.returnValue = returnValue;
  }

  /**
   * @param deserializer
   *          The deserializer to use
   */
  public OperationExecutionWithParametersRecord(final IValueDeserializer deserializer) {
    super(deserializer);
    // load array sizes
    int parameters_size0 = deserializer.getInt();
    this.parameterTypes = new String[parameters_size0];
    for (int i0 = 0; i0 < parameters_size0; i0++) {
      this.parameterTypes[i0] = deserializer.getString();
  }
    // load array sizes
    int values_size0 = deserializer.getInt();
    this.parameterValues = new String[values_size0];
    for (int i0 = 0; i0 < values_size0; i0++) {
      this.parameterValues[i0] = deserializer.getString();
    }
    
    this.returnType = deserializer.getString();
    this.returnValue = deserializer.getString();
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated since 1.13. Use {@link #serialize(IValueSerializer)} with an
   *             array serializer instead.
   */
  @Override
  @Deprecated
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Deprecated
  public void registerStrings(final IRegistry<String> stringRegistry) { // NOPMD (generated code)
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
    super.serialize(serializer);
    // store array sizes
    int parameters_size0 = this.getParameterTypes().length;
    serializer.putInt(parameters_size0);
    for (int i0 = 0; i0 < parameters_size0; i0++) {
      serializer.putString(this.getParameterTypes()[i0]);
  }
    // store array sizes
    int values_size0 = this.getParameterValues().length;
    serializer.putInt(values_size0);
    for (int i0 = 0; i0 < values_size0; i0++) {
      serializer.putString(this.getParameterValues()[i0]);
    }

    serializer.putString(this.getReturnType());
    serializer.putString(this.getReturnValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?>[] getValueTypes() {
    return TYPES; // NOPMD
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getValueNames() {
    return PROPERTY_NAMES; // NOPMD
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSize() {
    return SIZE;
  }

  /**
   * {@inheritDoc}
   * 
   * @deprecated This record uses the
   *             {@link kieker.common.record.IMonitoringRecord.Factory} mechanism.
   *             Hence, this method is not implemented.
   */
  @Deprecated
  @Override
  public void initFromArray(final Object[] values) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != this.getClass()) {
      return false;
    }

    final OperationExecutionWithParametersRecord castedRecord = (OperationExecutionWithParametersRecord) obj;
    if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
      return false;
    }
    if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) {
      return false;
    }
    if (!this.getSessionId().equals(castedRecord.getSessionId())) {
      return false;
    }
    if (this.getTraceId() != castedRecord.getTraceId()) {
      return false;
    }
    if (this.getTin() != castedRecord.getTin()) {
      return false;
    }
    if (this.getTout() != castedRecord.getTout()) {
      return false;
    }
    if (!this.getHostname().equals(castedRecord.getHostname())) {
      return false;
    }
    if (this.getEoi() != castedRecord.getEoi()) {
      return false;
    }
    if (this.getEss() != castedRecord.getEss()) {
      return false;
    }
    if (!this.getReturnType().equals(castedRecord.getReturnType())) {
      return false;
    }
    if (!this.getReturnValue().equals(castedRecord.getReturnValue())) {
      return false;
    }
    // get array length
    int _parameters_size0 = this.getParameterTypes().length;
    if (_parameters_size0 != castedRecord.getParameterTypes().length) {
      return false;
    }
    for (int i0 = 0; i0 < _parameters_size0; i0++) {
      if (!this.getParameterTypes()[i0].equals(castedRecord.getParameterTypes()[i0])) {
        return false;
      }
    }
    // get array length
    int values_size0 = this.getParameterValues().length;
    if (values_size0 != castedRecord.getParameterValues().length) {
      return false;
    }
    for (int i0 = 0; i0 < values_size0; i0++) {
      if (!this.getParameterValues()[i0].equals(castedRecord.getParameterValues()[i0])) {
        return false;
      }
    }
    return true;
  }

  public final String[] getParameterTypes() {
    return this.parameterTypes;
  }

  public final String[] getParameterValues() {
    return this.parameterValues;
  }

  public String getReturnType() {
    return this.returnType;
  }

  public String getReturnValue() {
    return this.returnValue;
  }

}
