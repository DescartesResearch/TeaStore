package tools.descartes.petsupplystore.registryclient.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

public class TrackingFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(TrackingFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String trackingInfo = httpRequest.getHeader("KiekerTracingInfo");
			if (trackingInfo != null && !trackingInfo.equals(""))
				LOG.info(trackingInfo);
		} else {
			LOG.error("Something went wrong");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
