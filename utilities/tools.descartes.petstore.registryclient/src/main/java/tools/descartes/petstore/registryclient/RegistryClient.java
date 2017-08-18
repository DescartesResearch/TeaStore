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
package tools.descartes.petstore.registryclient;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import com.netflix.loadbalancer.Server;

/**
 * Client with common functionality for registering with the registry.
 * @author Joakim von Kistowski
 *
 */
public final class RegistryClient {

	/**
	 * The registry client.
	 */
	public static final RegistryClient CLIENT = new RegistryClient();
	private String registryRESTURL;
	
	private Server myServiceInstanceServer = null;
	private Service myService = null;
	
	private RegistryClient() {
		try {
			registryRESTURL = (String) new InitialContext().lookup("java:comp/env/registryURL");
		} catch (NamingException e) {
			System.out.println("registryURL not set. Falling back to default registry URL.");
			registryRESTURL = "http://localhost:8080/tools.descartes.petstore.registry/rest/services/";
		}
	}
	
	/**
	 * Get all servers for a service in the {@link Service} enum from the registry.
	 * @param targetService The service for which to get the servers.
	 * @return List of servers.
	 */
	public List<Server> getServersForService(Service targetService) {
		Response response = getRESTClient().target(registryRESTURL)
				.path("/" + targetService.getServiceName() + "/")
				.request(MediaType.APPLICATION_JSON).get();
		List<String> list = response.readEntity(new GenericType<List<String>>() { }); 
		List<Server> serverList = new ArrayList<Server>();
		for (String string: list) {
			serverList.add(new Server(string));
		}
			
		return serverList;
	}
	
	/**
	 * Register a new server for a service in the registry.
	 * @param service The service for which to register.
	 * @param server The server address.
	 * @return True, if registration succeeded.
	 */
	public boolean register(Service service, Server server) {
		myService = service;
		myServiceInstanceServer = server;
		Response response = getRESTClient().target(registryRESTURL)
				.path(service.getServiceName()).path(server.toString())
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		return (response.getStatus() == Response.Status.OK.getStatusCode());
	}
	
	/**
	 * Unregister a server for a service in the registry.
	 * @param service The service for which to unregister.
	 * @param server The server address to remove.
	 * @return True, if unregistration succeeded.
	 */
	public boolean unregister(Service service, Server server) {
		Response response = getRESTClient().target(registryRESTURL)
				.path(service.getServiceName()).path(server.toString())
				.request(MediaType.APPLICATION_JSON).delete();
		return (response.getStatus() == Response.Status.OK.getStatusCode());
	}
	
	private Client getRESTClient() {
		ClientConfig configuration = new ClientConfig();
		configuration.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		configuration.property(ClientProperties.READ_TIMEOUT, 5000);
		return ClientBuilder.newClient(configuration);
	}

	/**
	 * Get the server for this service.
	 * Returns null if the service is not registered yet.
	 * @return The server for this service. Null, if not registered.
	 */
	public Server getMyServiceInstanceServer() {
		return myServiceInstanceServer;
	}

	/**
	 * Get the service of this application.
	 * Returns null if the service is not registered yet.
	 * @return The service for this application. Null, if not registered.
	 */
	public Service getMyService() {
		return myService;
	}
}
