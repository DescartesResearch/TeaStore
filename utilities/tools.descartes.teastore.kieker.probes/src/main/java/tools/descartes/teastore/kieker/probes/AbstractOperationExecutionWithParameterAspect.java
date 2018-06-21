/***************************************************************************
 * Copyright 2018 iObserve Project (https://www.iobserve-devops.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package tools.descartes.teastore.kieker.probes;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;
import kieker.monitoring.probe.aspectj.AbstractAspectJProbe;
import kieker.monitoring.timer.ITimeSource;
import tools.descartes.teastore.kieker.events.EntryLevelBeforeOperationEvent;

/**
 * Based on Jan Waller's
 * kieker.monitoring.probe.aspectj.flow.operationExecution.AbstractAspect class.
 * Took this class from the IObserve project (https://www.iobserve-devops.net)
 * in order to realize parameter logging with Kieker.
 *
 * @author Reiner Jung, Johannes Grohmann
 *
 * @since 0.0.3
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

		/** extension over the original routine. */
		final String[] names = ((MethodSignature) thisJoinPoint.getSignature()).getParameterNames();

		final Object[] arguments = thisJoinPoint.getArgs();
		final String[] values = new String[arguments.length];

		int i = 0;
		for (final Object argument : arguments) {
			values[i++] = argument.toString();
		}

		/** exchanged return type. */
		// measure before execution
		AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
				new EntryLevelBeforeOperationEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(),
						traceId, trace.getNextOrderId(), operationSignature, clazz, names, values, 0));
		// execution of the called method

		final Object retval;

		try {
			retval = thisJoinPoint.proceed();
		} catch (final Throwable th) { // NOPMD NOCS (catch throw might ok here)
			// measure after failed execution
			AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
					new AfterOperationFailedEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
							trace.getNextOrderId(), operationSignature, clazz, th.toString()));
			throw th;
		} finally {
			if (newTrace) { // close the trace
				AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.unregisterTrace();
			}
		}
		// measure after successful execution
		AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
				new AfterOperationEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
						trace.getNextOrderId(), operationSignature, clazz));
		return retval;
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

		// measure before execution
		AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
				new BeforeOperationEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
						trace.getNextOrderId(), operationSignature, clazz));

		// execution of the called method
		final Object retval;

		try {
			retval = thisJoinPoint.proceed();
		} catch (final Throwable th) { // NOPMD NOCS (catch throw might ok here)
			// measure after failed execution
			AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
					new AfterOperationFailedEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
							trace.getNextOrderId(), operationSignature, clazz, th.toString()));
			throw th;
		} finally {
			if (newTrace) { // close the trace
				AbstractOperationExecutionWithParameterAspect.TRACEREGISTRY.unregisterTrace();
			}
		}
		// measure after successful execution
		AbstractOperationExecutionWithParameterAspect.CTRLINST.newMonitoringRecord(
				new AfterOperationEvent(AbstractOperationExecutionWithParameterAspect.TIME.getTime(), traceId,
						trace.getNextOrderId(), operationSignature, clazz));
		return retval;
	}
}