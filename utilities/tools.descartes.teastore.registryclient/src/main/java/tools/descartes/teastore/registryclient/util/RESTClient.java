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
package tools.descartes.teastore.registryclient.util;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.UriBuilder;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Default Client that transfers Entities to/from a service that has a standard conforming REST-API.
 * @author Joakim von Kistowski
 * @param <T> Entity type for the client to handle.
 */
public class RESTClient<T> {

	/**
	 * Default and max size for connection pools. We estimate a good size by using the available processor count.
	 */

	private static final int DEFAULT_CONNECT_TIMEOUT = 600;
	private static final int DEFAULT_READ_TIMEOUT = 6000;

	/**
	 * Default REST application path.
	 */
	public static final String DEFAULT_REST_APPLICATION = "rest";

	private static int readTimeout = DEFAULT_READ_TIMEOUT;
	private static int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

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
		boolean useHTTPS = "true".equals(System.getenv("USE_HTTPS"));

		if (!hostURL.endsWith("/")) {
			hostURL += "/";
		}
		if (!hostURL.contains("://")) {
			if (useHTTPS) {
				hostURL = "https://" + hostURL;
			} else {
				hostURL = "http://" + hostURL;
			}
		}
		ClientConfig config = new ClientConfig();
		config.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
		config.property(ClientProperties.READ_TIMEOUT, readTimeout);
		config.connectorProvider(new GrizzlyConnectorProvider());

		if (useHTTPS) {
			try {
				TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
					}

					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
				};
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				HostnameVerifier allHostsValid = (hostname, session) -> true;
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

				ClientBuilder builder = ClientBuilder.newBuilder().withConfig(config);
				builder.sslContext(sslContext);
				client = builder.build();
			} catch (NoSuchAlgorithmException | KeyManagementException e) {
				e.printStackTrace();
			}
		} else {
			client = ClientBuilder.newClient(config);
		}

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
	 * Sets the global read timeout for all REST clients of this service.
	 * @param readTimeout The read timeout in ms.
	 */
	public static void setGlobalReadTimeout(int readTimeout) {
		RESTClient.readTimeout = readTimeout;
	}

	/**
	 * Sets the global connect timeout for all REST clients of this service.
	 * @param connectTimeout The read timeout in ms.
	 */
	public static void setGlobalConnectTimeout(int connectTimeout) {
		RESTClient.connectTimeout = connectTimeout;
	}

	/**
	 * Generic type of return lists.
	 * @return Generic List type.
	 */
	public GenericType<List<T>> getGenericListType() {
		return genericListType;
	}

	/**
	 * Class of entities to handle in REST Client.
	 * @return Entity class.
	 */
	public Class<T> getEntityClass() {
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
