package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.core.Response;

/**
 * Wrapper for external call returns.
 * @author Simon
 *
 */
public final class ResponseWrapper {

  /**
   * hides constructor.
   */
  private ResponseWrapper() {
    
  }
  
	/**
	 * Hook for monitoring.
	 * @param response response
	 * @return response response
	 */
	public static Response wrap(Response response) {
		return response;
	}

}
