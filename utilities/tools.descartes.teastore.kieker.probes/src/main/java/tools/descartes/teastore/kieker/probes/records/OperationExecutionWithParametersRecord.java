package tools.descartes.teastore.kieker.probes.records;

import java.nio.BufferOverflowException;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;
import kieker.common.util.registry.IRegistry;

/**
 * Class to store parameter logging with Kieker. Took parts of this class from
 * the IObserve project (https://www.iobserve-devops.net).
 * 
 * @author Johannes Grohmann, Reiner Jung API compatibility: Kieker 1.14.0
 * 
 * @since 1.0
 */
public class OperationExecutionWithParametersRecord extends AbstractOperationEvent implements IPayloadCharacterization {
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_LONG // IEventRecord.timestamp
			+ TYPE_SIZE_LONG // ITraceRecord.traceId
			+ TYPE_SIZE_INT // ITraceRecord.orderIndex
			+ TYPE_SIZE_STRING // IOperationSignature.operationSignature
			+ TYPE_SIZE_STRING // IClassSignature.classSignature
			+ TYPE_SIZE_STRING // IPayloadCharacterization.parameters
			+ TYPE_SIZE_STRING // IPayloadCharacterization.values
			+ TYPE_SIZE_STRING // IPayloadCharacterization.returnType
			+ TYPE_SIZE_STRING // IPayloadCharacterization.returnVal
			+ TYPE_SIZE_INT; // IPayloadCharacterization.requestType

	public static final Class<?>[] TYPES = { long.class, // IEventRecord.timestamp
			long.class, // ITraceRecord.traceId
			int.class, // ITraceRecord.orderIndex
			String.class, // IOperationSignature.operationSignature
			String.class, // IClassSignature.classSignature
			String[].class, // IPayloadCharacterization.parameters
			String[].class, // IPayloadCharacterization.values
			String.class, // IPayloadCharacterization.returnType
			String.class, // IPayloadCharacterization.returnVal
			int.class, // IPayloadCharacterization.requestType
	};

	private static final long serialVersionUID = -3583783831259543534L;

	/** property name array. */
	private static final String[] PROPERTY_NAMES = { "timestamp", "traceId", "orderIndex", "operationSignature",
			"classSignature", "parameters", "values", "returnType", "returnVal", "requestType", };

	/** property declarations. */
	private final String[] parameters;
	private final String[] values;
	private final String returnType;
	private final String returnVal;
	private final int requestType;

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param timestamp
	 *            timestamp
	 * @param traceId
	 *            traceId
	 * @param orderIndex
	 *            orderIndex
	 * @param operationSignature
	 *            operationSignature
	 * @param classSignature
	 *            classSignature
	 * @param parameters
	 *            parameters
	 * @param values
	 *            values
	 * @param returnType
	 *            the return type
	 * @param returnVal
	 *            return value
	 * @param requestType
	 *            requestType
	 */
	public OperationExecutionWithParametersRecord(final long timestamp, final long traceId, final int orderIndex,
			final String operationSignature, final String classSignature, final String[] parameters,
			final String[] values, final String returnType, final String returnVal, final int requestType) {
		super(timestamp, traceId, orderIndex, operationSignature, classSignature);
		this.parameters = parameters;
		this.values = values;
		this.returnType = returnType;
		this.returnVal = returnVal;
		this.requestType = requestType;
	}

	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException
	 *             when the record could not be deserialized
	 */
	public OperationExecutionWithParametersRecord(final IValueDeserializer deserializer)
			throws RecordInstantiationException {
		super(deserializer);
		// load array sizes
		int _parameters_size0 = deserializer.getInt();
		this.parameters = new String[_parameters_size0];
		for (int i0 = 0; i0 < _parameters_size0; i0++)
			this.parameters[i0] = deserializer.getString();

		// load array sizes
		int _values_size0 = deserializer.getInt();
		this.values = new String[_values_size0];
		for (int i0 = 0; i0 < _values_size0; i0++)
			this.values[i0] = deserializer.getString();

		this.returnType = deserializer.getString();
		this.returnVal = deserializer.getString();
		this.requestType = deserializer.getInt();
	}

	/**
	 * {@inheritDoc}
	 */
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		// super.serialize(serializer);
		serializer.putLong(this.getTimestamp());
		serializer.putLong(this.getTraceId());
		serializer.putInt(this.getOrderIndex());
		serializer.putString(this.getOperationSignature());
		serializer.putString(this.getClassSignature());
		// store array sizes
		int _parameters_size0 = this.getParameters().length;
		serializer.putInt(_parameters_size0);
		for (int i0 = 0; i0 < _parameters_size0; i0++)
			serializer.putString(this.getParameters()[i0]);

		// store array sizes
		int _values_size0 = this.getValues().length;
		serializer.putInt(_values_size0);
		for (int i0 = 0; i0 < _values_size0; i0++)
			serializer.putString(this.getValues()[i0]);

		serializer.putString(this.getReturnType());
		serializer.putString(this.getReturnValue());
		serializer.putInt(this.getRequestType());
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getValueNames() {
		return PROPERTY_NAMES; // NOPMD
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSize() {
		return SIZE;
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
		if (this.getTimestamp() != castedRecord.getTimestamp()) {
			return false;
		}
		if (this.getTraceId() != castedRecord.getTraceId()) {
			return false;
		}
		if (this.getOrderIndex() != castedRecord.getOrderIndex()) {
			return false;
		}
		if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) {
			return false;
		}
		if (!this.getClassSignature().equals(castedRecord.getClassSignature())) {
			return false;
		}
		if (!this.getReturnType().equals(castedRecord.getReturnType())) {
			return false;
		}
		if (!this.getReturnValue().equals(castedRecord.getReturnValue())) {
			return false;
		}
		// get array length
		int _parameters_size0 = this.getParameters().length;
		if (_parameters_size0 != castedRecord.getParameters().length) {
			return false;
		}
		for (int i0 = 0; i0 < _parameters_size0; i0++)
			if (!this.getParameters()[i0].equals(castedRecord.getParameters()[i0])) {
				return false;
			}

		// get array length
		int _values_size0 = this.getValues().length;
		if (_values_size0 != castedRecord.getValues().length) {
			return false;
		}
		for (int i0 = 0; i0 < _values_size0; i0++)
			if (!this.getValues()[i0].equals(castedRecord.getValues()[i0])) {
				return false;
			}

		if (this.getRequestType() != castedRecord.getRequestType()) {
			return false;
		}

		return true;
	}

	public final String[] getParameters() {
		return this.parameters;
	}

	public final String[] getValues() {
		return this.values;
	}

	public final int getRequestType() {
		return this.requestType;
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerStrings(final IRegistry<String> stringRegistry) { // NOPMD (generated code)
		stringRegistry.get(this.getOperationSignature());
		stringRegistry.get(this.getClassSignature());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the
	 *             {@link kieker.common.record.IMonitoringRecord.Factory} mechanism.
	 *             Hence, this method is not implemented.
	 */
	@Override
	@Deprecated
	public void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated since 1.13. Use {@link #serialize(IValueSerializer)} with an
	 *             array serializer instead.
	 */
	@Deprecated
	public Object[] toArray() {
		throw new UnsupportedOperationException();
		// return new Object[] {
		// this.getTimestamp(),
		// this.getTraceId(),
		// this.getOrderIndex(),
		// this.getOperationSignature(),
		// this.getClassSignature()
		// };
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String getReturnValue() {
		return this.returnVal;
	}

}