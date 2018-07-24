package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;

import javax.ws.rs.client.WebTarget;

/**
 * Wrapper for http calls.
 * @author Simon
 *
 */
public final class HttpWrapper {
  private static final Log LOG = LogFactory.getLog(HttpWrapper.class);
  private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
  private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
  private static final String HEADER_FIELD = "KiekerTracingInfo";

  /**
   * Hide default constructor.
   */
  private HttpWrapper() {

  }

  /**
   * Wrap webtarget
   * @param target webtarget to wrap
   * @return wrapped wentarget
   */
  public static Builder wrap(WebTarget target) {
    // boolean entrypoint = true;
    // final String hostname = VMNAME;
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
    Builder builder = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

    return builder.header(HEADER_FIELD, Long.toString(traceId) + "," + sessionId + ","
        + Integer.toString(eoi) + "," + Integer.toString(nextESS));

  }
}
