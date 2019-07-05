package tools.descartes.teastore.kieker.probes;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;
import kieker.monitoring.probe.aspectj.AbstractAspectJProbe;
import kieker.monitoring.timer.ITimeSource;
import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.kieker.probes.records.OperationExecutionWithParametersRecord;

/**
 * Probe to log execution times plus parameter values with Kieker.
 *
 * @author Johannes Grohmann
 *
 */
@Aspect
public abstract class AbstractOperationExecutionWithParameterAspect extends AbstractAspectJProbe {
  private static final Logger LOG = LoggerFactory
      .getLogger(AbstractOperationExecutionWithParameterAspect.class);

  private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
  private static final ITimeSource TIME = CTRLINST.getTimeSource();
  private static final String VMNAME = CTRLINST.getHostname();
  private static final ControlFlowRegistry CFREGISTRY = ControlFlowRegistry.INSTANCE;
  private static final SessionRegistry SESSIONREGISTRY = SessionRegistry.INSTANCE;

  /**
   * The pointcut for the monitored operations. Inheriting classes should extend
   * the pointcut in order to find the correct executions of the methods (e.g. all
   * methods or only methods with specific annotations).
   */
  @Pointcut
  public abstract void monitoredOperation();

  /**
   * actual pointcut.
   * @param thisJoinPoint joinpoint
   * @return result of joinpoint
   * @throws Throwable possible errors
   */
  @Around("monitoredOperation() && notWithinKieker()")
  public Object operation(final ProceedingJoinPoint thisJoinPoint) throws Throwable { // NOCS
                                                                                      // (Throwable)
    if (!CTRLINST.isMonitoringEnabled()) {
      return thisJoinPoint.proceed();
    }
    final String signature = this.signatureToLongString(thisJoinPoint.getSignature());
    if (!CTRLINST.isProbeActivated(signature)) {
      return thisJoinPoint.proceed();
    }
    // collect data
    final boolean entrypoint;
    final String hostname = VMNAME;
    final String sessionId = SESSIONREGISTRY.recallThreadLocalSessionId();
    final int eoi; // this is executionOrderIndex-th execution in this trace
    final int ess; // this is the height in the dynamic call tree of this execution
    long traceId = CFREGISTRY.recallThreadLocalTraceId(); // traceId, -1 if entry point
    if (traceId == -1) {
      entrypoint = true;
      traceId = CFREGISTRY.getAndStoreUniqueThreadLocalTraceId();
      CFREGISTRY.storeThreadLocalEOI(0);
      CFREGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
      eoi = 0;
      ess = 0;
    } else {
      entrypoint = false;
      eoi = CFREGISTRY.incrementAndRecallThreadLocalEOI(); // ess > 1
      ess = CFREGISTRY.recallAndIncrementThreadLocalESS(); // ess >= 0
      if ((eoi == -1) || (ess == -1)) {
        LOG.error("eoi and/or ess have invalid values:" + " eoi == " + eoi + " ess == " + ess);
        CTRLINST.terminateMonitoring();
      }
    }
    // measure before
    final long tin = TIME.getTime();
    // execution of the called method
    Object retval = null;
    try {
      retval = thisJoinPoint.proceed();
    } finally {
      // measure after
      final long tout = TIME.getTime();
      // get parameters

      String flag = System.getenv("LOG_PARAMETERS");
      if (flag != null && (flag.equals("true") || flag.equals("TRUE"))) {
        logWithParameter(thisJoinPoint, signature, sessionId, traceId, tin, tout, hostname, eoi,
            ess, retval);
      } else {
        logWithoutParameters(thisJoinPoint, signature, sessionId, traceId, tin, tout, hostname, eoi,
            ess, retval);
      }

      // cleanup
      if (entrypoint) {
        CFREGISTRY.unsetThreadLocalTraceId();
        CFREGISTRY.unsetThreadLocalEOI();
        CFREGISTRY.unsetThreadLocalESS();
      } else {
        CFREGISTRY.storeThreadLocalESS(ess); // next operation is ess
      }
    }
    return retval;
  }

  private void logWithParameter(final ProceedingJoinPoint thisJoinPoint, String signature,
      String sessionId, long traceId, long tin, long tout, String hostname, int eoi, int ess,
      Object retval) {
    /** extension over the original routine. */
    final String[] names = ((MethodSignature) thisJoinPoint.getSignature()).getParameterNames();

    final Object[] arguments = thisJoinPoint.getArgs();
    final String[] values = new String[arguments.length];

    int i = 0;
    for (final Object argument : arguments) {
      values[i] = parseObjectToString(argument);
      if (argument instanceof java.util.Collection && !names[i].endsWith(".size()")) {
        names[i] = names[i] + ".size()";
      }
      i++;
    }
    // get return type
    Class<?> returnClass = ((MethodSignature) thisJoinPoint.getSignature()).getReturnType();
    final String returnType;
    final String returnValue;
    if (returnClass.equals(Void.TYPE)) {
      // return type is void
      returnType = "void";
      returnValue = "";
    } else {
      // we have a return type
      returnType = returnClass.getName();
      returnValue = parseObjectToString(retval);
    }

    CTRLINST.newMonitoringRecord(new OperationExecutionWithParametersRecord(signature, sessionId,
        traceId, tin, tout, hostname, eoi, ess, names, values, returnType, returnValue));

  }

  private String parseObjectToString(Object argument) {
    if (argument == null) {
      return "null";
    }
    if (argument instanceof java.util.Collection) {
      // log collection size
      return String.valueOf(((java.util.Collection<?>) argument).size());
    }
    if (argument instanceof SessionBlob) {
      SessionBlob sb = (SessionBlob) argument;
      return String.valueOf(sb.getOrderItems().size());
    }

    if (argument instanceof ImageSize) {
      return String.valueOf(((ImageSize) argument).getPixelCount());
    }

    // all others are just to string
    String stringRepresentation = argument.toString();
    int stringLength = stringRepresentation.length();
    if (stringLength < 500) {
      return argument.toString();
    }
    return String.valueOf(stringLength);
  }

  private void logWithoutParameters(final ProceedingJoinPoint thisJoinPoint, String signature,
      String sessionId, long traceId, long tin, long tout, String hostname, int eoi, int ess,
      Object retval) {
    CTRLINST.newMonitoringRecord(
        new OperationExecutionRecord(signature, sessionId, traceId, tin, tout, hostname, eoi, ess));

  }
}
