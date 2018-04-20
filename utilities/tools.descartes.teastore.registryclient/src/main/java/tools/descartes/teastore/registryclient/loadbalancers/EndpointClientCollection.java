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
package tools.descartes.teastore.registryclient.loadbalancers;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.netflix.loadbalancer.Server;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.util.RESTClient;

/**
 * Load balancer for a REST endpoint. Balances between the different servers.
 * @author Joakim von Kistowski
 * @param <T> Type of the entity class returned by the endpoint.
 */
public class EndpointClientCollection<T> {
	
	 //load balancer for each endpoint has REST clients for each server
    private ConcurrentHashMap<Server, RESTClient<T>> clients = new ConcurrentHashMap<>();
    
    
    private final Class<T> entityClass;
	private final Service targetService;
	private final String endpointURI;
    
	/**
	 * Create a new endpoint client collection.
	 * @param targetService The service to address.
	 * @param endpointURI The endpoint URI (e.g., "products").
	 * @param entityClass The class of the entities to pass around.
	 */
	EndpointClientCollection(Service targetService, String endpointURI, final Class<T> entityClass) {
		this.endpointURI = endpointURI;
		this.targetService = targetService;
		this.entityClass = entityClass;
	}
	
	/**
	 * Checks for updates in the list of relevant servers.
	 * Rebuilds the load balancer if server list changed.
	 * @param newServers The newly received list from the registry.
	 */
	void updateServers(Collection<Server> newServers) {
		Set<Server> oldServers = clients.keySet();
		//don't do anything if nothing changed
		if (oldServers.size() == newServers.size() && newServers.containsAll(oldServers)) {
			return;
		}
		updateClients(newServers);
	}
	
	/**
	 * Get the endpoint URI.
	 * @return The REST enpoint URI (e.g., "products").
	 */
	public String getEndpointURI() {
		return endpointURI;
	}
	
	private void updateClients(Collection<Server> newServers) {
    	//remove outdated clients
    	for (Server s : clients.keySet()) {
    		if (!newServers.contains(s)) {
    			clients.remove(s);
    		}
    	}
    	//add new clients
    	for (Server s : newServers) {
    		clients.putIfAbsent(s, new RESTClient<T>(s.getHost() + ":"
    					+ s.getPort() + "/" + targetService.getServiceName(),
					RESTClient.DEFAULT_REST_APPLICATION, endpointURI, entityClass));
    	}
    }
	
	/**
	 * Gets the rest client for a server. Returns null if it doesnt exist.
	 * @param server The server for which to get the client.
	 * @return The server.
	 */
	RESTClient<T> getRESTClient(Server server) {
		return clients.get(server);
	}
	
//	public <R> R loadBalanceRESTOperation(Function<AbstractRESTClient<T>, R> operation) {
//		return LoadBalancerCommand.<R>builder()
//                .withLoadBalancer(loadBalancer)
//                .withRetryHandler(retryHandler)
//                .build()
//                .submit(server -> Observable.just(operation.apply(clients.get(server))))
//                .toBlocking().first();
//	}
	
}
