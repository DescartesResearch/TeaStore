package tools.descartes.teastore.kieker.probes;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;
import kieker.monitoring.probe.aspectj.AbstractAspectJProbe;
import kieker.monitoring.timer.ITimeSource;
import tools.descartes.teastore.kieker.events.OperationExecutionWithParametersRecord;

/**
 * Based on Jan Waller's
 * kieker.monitoring.probe.aspectj.flow.operationExecution.AbstractAspect class.
 * Took parts from this class from the IObserve project
 * (https://www.iobserve-devops.net). in order to realize parameter logging with
 * Kieker. This class represents the probe, that does the actual logging.
 *
 * @author Johannes Grohmann, Reiner Jung
 *
 * @since 1.0
 */
@Aspect
public abstract class AbstractOperationExecutionWithParameterAspect extends AbstractAspectJProbe { // NOPMD

	private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
	private static final ITimeSource TIME = AbstractOperationExecutionWithParameterAspect.CTRLINST.getTimeSource();
	private static final TraceRegistry TRACEREGISTRY = TraceRegistry.INSTANCE;

	/**
	 * The pointcut for the monitored operations. Inheriting classes should extend
	 * the pointcut in order to find the correct executions of the methods (e.g. all
	 * methods or only methods with specific annotations).
	 */
	@Pointcut
	public abstract void monitoredOperation();

	@Around("monitoredOperation() && this(thisObject) && notWithinKieker()")
	public Object operation(final Object thisObject, final ProceedingJoinPoint thisJoinPoint) throws Throwable { // NOCS
																													// (Throwable)
		if (!AbstractOperationExecutionWithParameterAspect.CTRLINST.isMonitoringEnabled()) {
			return thisJoinPoint.proceed();
		}
		final String operationSignature = this.signatureToLongString(thisJoinPoint.getSignature());
		if (!AbstractOperationExecutionWithParameterAspect.CTRLINST.isProbeActivated(operationSignature)) {
			return thisJoinPoint.proceed();
		}

		// common fields
		TraceMetadata trace = AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.getTrace();
		final boolean newTrace = trace == null;
		if (newTrace) {
			trace = AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.registerTrace();
			AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(trace);
		}

		final long traceId = trace.getTraceId();
		final String clazz = thisObject.getClass().getName();
		return logOperation(thisJoinPoint, traceId, trace, operationSignature, clazz, newTrace);

	}

	@Around("monitoredOperation() && !this(java.lang.Object) && notWithinKieker()")
	public Object staticOperation(final ProceedingJoinPoint thisJoinPoint) throws Throwable { // NOCS
																								// (Throwable)
		if (!AbstractOperationExecutionWithParameterAspect.CTRLINST.isMonitoringEnabled()) {
			return thisJoinPoint.proceed();
		}
		final Signature sig = thisJoinPoint.getSignature();
		final String operationSignature = this.signatureToLongString(sig);
		if (!AbstractOperationExecutionWithParameterAspect.CTRLINST.isProbeActivated(operationSignature)) {
			return thisJoinPoint.proceed();
		}

		// common fields
		TraceMetadata trace = AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.getTrace();
		final boolean newTrace = trace == null;
		if (newTrace) {
			trace = AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.registerTrace();
			AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(trace);
		}

		final long traceId = trace.getTraceId();
		final String clazz = sig.getDeclaringTypeName();

		return logOperation(thisJoinPoint, traceId, trace, operationSignature, clazz, newTrace);
	}

	// Just a wrapper to save some space
	private Object logOperation(final ProceedingJoinPoint thisJoinPoint, long traceId, TraceMetadata trace,
			String operationSignature, String clazz, boolean newTrace) throws Throwable {

		/** extension over the original routine. */
		final String[] names = ((MethodSignature) thisJoinPoint.getSignature()).getParameterNames();

		final Object[] arguments = thisJoinPoint.getArgs();
		final String[] values = new String[arguments.length];

		int i = 0;
		for (final Object argument : arguments) {
			values[i++] = argument.toString();
		}

		/** exchanged return type. */
		// execution of the called method

		final Object retval;

		try {
			retval = thisJoinPoint.proceed();
		} catch (final Throwable th) { // NOPMD NOCS (catch throw might ok here)
			// measure after failed execution
			AbstractOperationExecutionWithParameterAspect.CTRLINST
					.newMonitoringRecord(new OperationExecutionWithParametersRecord(
							AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
							trace.getNextOrderId(), operationSignature, clazz, names, values, null, null, 1));
			throw th;
		} finally {
			if (newTrace) { // close the trace
				AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.unregisterTrace();
			}
		}
		// measure after successful execution
		if (retval != null) {
			AbstractOperationExecutionWithParameterAspect.CTRLINST
					.newMonitoringRecord(new OperationExecutionWithParametersRecord(
							AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
							trace.getNextOrderId(), operationSignature, clazz, names, values,
							retval.getClass().toString(), retval.toString(), 0));
		} else {
			AbstractOperationExecutionWithParameterAspect.CTRLINST
					.newMonitoringRecord(new OperationExecutionWithParametersRecord(
							AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
							trace.getNextOrderId(), operationSignature, clazz, names, values, null, null, 1));
		}
		return retval;
	}
}