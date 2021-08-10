package tools.descartes.teastore.registryclient.rest;

import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;

/**
 * Wrapper for http responses.
 *
 * @author Simon
 *
 */
public final class ResponseWrapper {

  private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
  private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
  private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
  private static final String HEADER_FIELD = "KiekerTracingInfo";
  private static final Logger LOG = LoggerFactory.getLogger(ResponseWrapper.class);
  private static final String SESSION_ID_ASYNC_TRACE = "NOSESSION-ASYNCIN";

  /**
   * Hide default constructor.
   */
  private ResponseWrapper() {

  }

  /**
   * Hook for monitoring.
   *
   * @param response
   *          response
   * @return response response
   */
  public static Response wrap(Response response) {
    if (CTRLINST.isMonitoringEnabled()) {
      long traceId = -1L;
      int eoi;
      int ess;
      String sessionId;

      final String operationExecutionHeader = response.getHeaderString(HEADER_FIELD);
      if ((operationExecutionHeader == null) || (operationExecutionHeader.equals(""))) {
        LOG.warn("Response without tracking id was found");
      } else {

        final String[] headerArray = operationExecutionHeader.split(",");

        // Extract session id
        sessionId = headerArray[1];
        if ("null".equals(sessionId)) {
          sessionId = OperationExecutionRecord.NO_SESSION_ID;
        }

        // Extract EOI
        final String eoiStr = headerArray[2];
        eoi = -1;
        try {
          eoi = Integer.parseInt(eoiStr);
        } catch (final NumberFormatException exc) {
          LOG.warn("Invalid eoi", exc);
        }

        // Extract ESS
        final String essStr = headerArray[3];
        ess = -1;
        try {
          ess = Integer.parseInt(essStr);
        } catch (final NumberFormatException exc) {
          LOG.warn("Invalid ess", exc);
        }

        // Extract trace id
        final String traceIdStr = headerArray[0];
        if (traceIdStr != null) {
          try {
            traceId = Long.parseLong(traceIdStr);
          } catch (final NumberFormatException exc) {
            LOG.warn("Invalid trace id", exc);
          }
        } else {
          traceId = CF_REGISTRY.getUniqueTraceId();
          sessionId = SESSION_ID_ASYNC_TRACE;
          eoi = 0; // EOI of this execution
          ess = 0; // ESS of this execution
        }

        // Store thread-local values
        CF_REGISTRY.storeThreadLocalTraceId(traceId);
        CF_REGISTRY.storeThreadLocalEOI(eoi); // this execution has EOI=eoi; next execution will get
                                              // eoi with incrementAndRecall
        CF_REGISTRY.storeThreadLocalESS(ess); // this execution has ESS=ess
        SESSION_REGISTRY.storeThreadLocalSessionId(sessionId);
      }
    }
    return response;
  }

}
