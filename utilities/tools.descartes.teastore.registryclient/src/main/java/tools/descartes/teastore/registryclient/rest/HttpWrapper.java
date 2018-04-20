package tools.descartes.teastore.registryclient.rest;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.client.WebTarget;

public class HttpWrapper {

	public static Builder wrap(WebTarget target) {
		Builder builder = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

		return builder;
	}
}
