package tools.descartes.petsupplystore.registryclient.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class TrackingFilter implements Filter {

	Logger logger = Logger.getLogger(TrackingFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		BasicConfigurator.configure();
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			logger.info(httpRequest.getHeader("KiekerTracingInfo"));
		} else {
			logger.error("Something went wrong");
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
