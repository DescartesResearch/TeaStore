package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.core.Response;

public class ResponseWrapper {

	/**
	 * Hook for monitoring
	 * @param response
	 * @return response
	 */
	public static Response wrap(Response response) {
		return response;
	}

}
