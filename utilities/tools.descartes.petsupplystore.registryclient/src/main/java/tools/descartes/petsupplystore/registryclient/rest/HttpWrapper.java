package tools.descartes.petsupplystore.registryclient.rest;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.WebTarget;

public class HttpWrapper {

	public static Builder wrap(WebTarget target) {
		return target.request(MediaType.APPLICATION_JSON).header("asd", "").accept(MediaType.APPLICATION_JSON);
	}
}
