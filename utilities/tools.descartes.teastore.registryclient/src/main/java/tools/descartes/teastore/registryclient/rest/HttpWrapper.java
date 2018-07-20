package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.client.WebTarget;

/**
 * Wrapper for http calls, acts as an extension point.
 * @author Simon
 *
 */
public final class HttpWrapper {

  private HttpWrapper() {
    
  }
  
  /**
   * Wrapper for http calls.
   * @param target webtarget
   * @return builder
   */
	public static Builder wrap(WebTarget target) {
		Builder builder = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		return builder;
	}
}
