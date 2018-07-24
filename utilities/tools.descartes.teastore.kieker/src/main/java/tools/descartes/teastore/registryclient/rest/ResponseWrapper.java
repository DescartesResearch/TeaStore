package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.core.Response;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;

public class ResponseWrapper {

	private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
	private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
	public static final String HEADER_FIELD = "KiekerTracingInfo";
	private static final Log LOG = LogFactory.getLog(ResponseWrapper.class);
	public static final String SESSION_ID_ASYNC_TRACE = "NOSESSION-ASYNCIN";
	/**
	 * Hook for monitoring
	 * @param response
	 * @return response
	 */
	public static Response wrap(Response response) {
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
		  CF_REGISTRY.storeThreadLocalEOI(eoi); // this execution has EOI=eoi; next execution will get eoi with incrementAndRecall
		  CF_REGISTRY.storeThreadLocalESS(ess); // this execution has ESS=ess
		  SESSION_REGISTRY.storeThreadLocalSessionId(sessionId);
	   }
    return response;
	}

}
