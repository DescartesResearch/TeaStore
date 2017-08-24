/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.petsupplystore.rest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 * Default Client that transfers Entities to/from a service that has a standard conforming REST-API.
 * @author Joakim von Kistowski
 * @param <T> Entity type for the client to handle.
 */
public class RESTClient<T> {
	
	private static final int CONNECT_TIMEOUT = 750;
	private static final int READ_TIMEOUT = 4000;
	
	/**
	 * Default REST application path.
	 */
	public static final String DEFAULT_REST_APPLICATION = "rest";
	
	private String applicationURI;
	private String endpointURI;
	
	private Client client;
	private WebTarget service;
	private Class<T> entityClass;
	
	private ParameterizedType parameterizedGenericType;
	private GenericType<List<T>> genericListType;

	/**
	 * Creates a new REST Client for an entity of Type T. The client interacts with a Server providing
	 * CRUD functionalities
	 * @param hostURL The url of the host. Common Pattern: "http://[hostname]:[port]/servicename/"
	 * @param application The name of the rest application, usually {@link #DEFAULT_REST_APPLICATION} "rest" (no "/"!)
	 * @param endpoint The name of the rest endpoint, typically the all lower case name of the entity in a plural form.
	 * E.g., "products" for the entity "Product" (no "/"!)
	 * @param entityClass Classtype of the Entitiy to send/receive. Note that the use of this Class type is
	 * 			open for interpretation by the inheriting REST clients.
	 */
	public RESTClient(String hostURL, String application, String endpoint, final Class<T> entityClass) {
		if (!hostURL.endsWith("/")) {
			hostURL += "/";
		}
		if (!hostURL.contains("://")) {
			hostURL = "http://" + hostURL;
		}
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		client = ClientBuilder.newClient(config);
		service = client.target(UriBuilder.fromUri(hostURL).build());
		applicationURI = application;
		endpointURI = endpoint;
		this.entityClass = entityClass;
		
		parameterizedGenericType = new ParameterizedType() {
		        public Type[] getActualTypeArguments() {
		            return new Type[] { entityClass };
		        }

		        public Type getRawType() {
		            return List.class;
		        }

		        public Type getOwnerType() {
		            return List.class;
		        }
		    };
		    genericListType = new GenericType<List<T>>(parameterizedGenericType) { };
	}

	/**
	 * Generic type of return lists.
	 * @return Generic List type.
	 */
	GenericType<List<T>> getGenericListType() {
		return genericListType;
	}

	/**
	 * Class of entities to handle in REST Client.
	 * @return Entity class.
	 */
	Class<T> getEntityClass() {
		return entityClass;
	}
	
	/**
	 * The service to use.
	 * @return The web service.
	 */
	public WebTarget getService() {
		return service;
	}
	
	/**
	 * Get the web target for sending requests directly to the endpoint.
	 * @return The web target for the endpoint.
	 */
	public WebTarget getEndpointTarget() {
		return service.path(applicationURI).path(endpointURI);
	}

	/**
	 * URI of the REST Endpoint within the application.
	 * @return The enpoint URI.
	 */
	public String getEndpointURI() {
		return endpointURI;
	}
	
	/**
	 * URI of the rest application (usually "rest").
	 * @return The application URI.
	 */
	public String getApplicationURI() {
		return applicationURI;
	}
	
}
