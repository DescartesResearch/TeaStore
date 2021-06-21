package tools.descartes.teastore.registryclient.rest;

import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;
import tools.descartes.teastore.registryclient.tracing.Tracing;

import jakarta.ws.rs.client.WebTarget;

/**
 * Wrapper for http calls.
 *
 * @author Simon
 *
 */
public final class HttpWrapper {
  private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(HttpWrapper.class);
  private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
  private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
  private static final String HEADER_FIELD = "KiekerTracingInfo";

  /**
   * Hide default constructor.
   */
  private HttpWrapper() {

  }

  /**
   * Wrap webtarget.
   *
   * @param target webtarget to wrap
   * @return wrapped wentarget
   */
  public static Builder wrap(WebTarget target) {
    Builder builder = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    Tracing.inject(builder);
    if (CTRLINST.isMonitoringEnabled()) {
      final String sessionId = SESSION_REGISTRY.recallThreadLocalSessionId();
      final int eoi; // this is executionOrderIndex-th execution in this trace
      final int ess; // this is the height in the dynamic call tree of this execution
      final int nextESS;
      long traceId = CF_REGISTRY.recallThreadLocalTraceId(); // traceId, -1 if entry point
      if (traceId == -1) {
        // entrypoint = true;
        traceId = CF_REGISTRY.getAndStoreUniqueThreadLocalTraceId();
        CF_REGISTRY.storeThreadLocalEOI(0);
        CF_REGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
        eoi = 0;
        ess = 0;
        nextESS = 1;
      } else {
        // entrypoint = false;
        eoi = CF_REGISTRY.recallThreadLocalEOI();
        ess = CF_REGISTRY.recallThreadLocalESS();
        nextESS = ess;
        if ((eoi == -1) || (ess == -1)) {
          LOG.error("eoi and/or ess have invalid values:" + " eoi == " + eoi + " ess == " + ess);
          // CTRLINST.terminateMonitoring();
        }
      }
      // Get request header
      return builder.header(HEADER_FIELD,
          Long.toString(traceId) + "," + sessionId + "," + Integer.toString(eoi) + "," + Integer.toString(nextESS));
    }
    return builder;
  }
}
