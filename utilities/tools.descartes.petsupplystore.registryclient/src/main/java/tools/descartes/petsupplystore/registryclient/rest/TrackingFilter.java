package tools.descartes.petsupplystore.registryclient.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;

public class TrackingFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(TrackingFilter.class);


	public static final String SESSION_ID_ASYNC_TRACE = "NOSESSION-ASYNCIN";

//	private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
//	private static final ITimeSource TIME = CTRLINST.getTimeSource();
//	private static final String VMNAME = CTRLINST.getHostname();
	private static final ControlFlowRegistry CF_REGISTRY = ControlFlowRegistry.INSTANCE;
	private static final SessionRegistry SESSION_REGISTRY = SessionRegistry.INSTANCE;
	public static final String HEADER_FIELD = "KiekerTracingInfo";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest req = (HttpServletRequest) request;
//	        String signature;
	        String sessionId = SESSION_REGISTRY.recallThreadLocalSessionId();
	        long traceId = -1L;
//	        long tin;
//	        final String hostname = VMNAME;
	        int eoi;
	        int ess;
	
			final String operationExecutionHeader = req.getHeader(HEADER_FIELD);

			if ((operationExecutionHeader == null) || (operationExecutionHeader.equals(""))) {
				LOG.debug("No monitoring data found in the incoming request header");
				// LOG.info("Will continue without sending back reponse header");
				traceId = CF_REGISTRY.getAndStoreUniqueThreadLocalTraceId();
				CF_REGISTRY.storeThreadLocalEOI(0);
				CF_REGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
				eoi = 0;
				ess = 0;
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Received request: " + req.getMethod() + "with header = " + operationExecutionHeader);
				}
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
				CF_REGISTRY.storeThreadLocalESS(ess + 1); // this execution has ESS=ess
				SESSION_REGISTRY.storeThreadLocalSessionId(sessionId);
			}

			((HttpServletResponse)response).addHeader(HEADER_FIELD, traceId + "," + sessionId + "," + (eoi+1) + "," + Integer.toString(CF_REGISTRY.recallThreadLocalESS()));
		
		} else {
			LOG.error("Something went wrong");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		CF_REGISTRY.unsetThreadLocalTraceId();
		CF_REGISTRY.unsetThreadLocalEOI();
		CF_REGISTRY.unsetThreadLocalESS();
	}

}
