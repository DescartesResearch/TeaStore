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
package tools.descartes.petstore.registryclient.loadbalancers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;

import rx.Observable;
import tools.descartes.petstore.registryclient.RegistryClient;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.rest.RESTClient;

/**
 * The load balancer for services.
 * @author Joakim von Kistowski
 *
 */
public final class ServiceLoadBalancer {

	//Loadbalancers for each service name
	private static ConcurrentHashMap<String, ServiceLoadBalancer> serviceMap = new ConcurrentHashMap<>();
	
	//clients for each endpoint (for a fixed service)
	private ConcurrentHashMap<String, EndpointClientCollection<?>> endpointMap = new ConcurrentHashMap<>();
	private final Service targetService;
	private Set<Server> serviceServers = new HashSet<Server>();
	
	private BaseLoadBalancer loadBalancer;
    // retry handler that does not retry on same server, but on a different server
    private final RetryHandler retryHandler = new DefaultLoadBalancerRetryHandler(1, 2, true);
	
    private ReadWriteLock loadBalancerModificationLock = new ReentrantReadWriteLock();
    
    //private constructor
    private ServiceLoadBalancer(final Service targetService) {
    	this.targetService = targetService;
    }    
    
	private static ServiceLoadBalancer getServiceLoadBalancer(Service targetService) {
		ServiceLoadBalancer serviceBalancer = serviceMap.get(targetService.getServiceName());
    	if (serviceBalancer == null) {
    		serviceMap.putIfAbsent(targetService.getServiceName(), new ServiceLoadBalancer(targetService));
    		updateLoadBalancersForServiceUsingRegistry(targetService);
    	}
    	return serviceMap.get(targetService.getServiceName());
    }
	
	/**
	 * Gets the load balancer for a service. Initializes it with a list of know servers,
	 * if the service is not known exists.
	 * @param targetService The service for which to get the balancer
	 * @param knownServers The list of know servers.
	 * @return The load balancer.
	 */
	static ServiceLoadBalancer getServiceLoadBalancer(Service targetService, List<Server> knownServers) {
		ServiceLoadBalancer serviceBalancer = ServiceLoadBalancer.serviceMap.get(targetService.getServiceName());
    	if (serviceBalancer == null) {
    		serviceMap.putIfAbsent(targetService.getServiceName(), new ServiceLoadBalancer(targetService));
    		updateLoadBalancersForService(targetService, knownServers);
    	}
    	return serviceMap.get(targetService.getServiceName());
    }
	
	@SuppressWarnings("unchecked")
	private <T> EndpointClientCollection<T> getEndpointClientCollection(String endpointURI, Class<T> entityClass) {
		EndpointClientCollection<?> endpointCollection = endpointMap.get(endpointURI);
    	if (endpointCollection == null) {
    		endpointMap.putIfAbsent(endpointURI,
    				new EndpointClientCollection<T>(targetService, endpointURI, entityClass));
    		endpointMap.get(endpointURI).updateServers(serviceServers);
    	}
    	endpointCollection = endpointMap.get(endpointURI);
    	return (EndpointClientCollection<T>) endpointCollection;
	}
    
	/**
	 * Update all load balancers for a service. Triggers Registry client to ask registry for updates.
	 */
	static void updateLoadBalancersForKnownServicesUsingRegistry() {
		serviceMap.values().forEach(balancer -> updateLoadBalancersForServiceUsingRegistry(balancer.targetService));
    }
	
	/**
	 * Update all load balancers for a service with servers. Triggers Registry client to ask registry for updates.
	 * @param targetService The service for which to update.
	 */
    private static void updateLoadBalancersForServiceUsingRegistry(Service targetService) {
    	List<Server> servers = RegistryClient.CLIENT.getServersForService(targetService);
    	updateLoadBalancersForService(targetService, servers);
    }
	
	/**
	 * Update all load balancers for a service. Call if server list has changed.
	 * @param newServers New servers with which to update the load balancers.
	 * @param targetService The service for which to update.
	 */
    static void updateLoadBalancersForService(Service targetService, List<Server> newServers) {
    	ServiceLoadBalancer serviceBalancer = serviceMap.get(targetService.getServiceName());
    	if (serviceBalancer == null) {
    		return;
    	}
    	serviceBalancer.updateLoadBalancer(newServers);
    }
    
    private void updateLoadBalancer(List<Server> newServers) {
    	//return if nothing changed
    	if (newServers.size() == serviceServers.size() && serviceServers.containsAll(newServers)) {
    		return;
    	}
    	serviceServers = new HashSet<Server>(newServers);
    	loadBalancerModificationLock.writeLock().lock();
    	loadBalancer = LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(newServers);
    	for (EndpointClientCollection<?> lb : endpointMap.values()) {
    		lb.updateServers(newServers);
    	}
    	loadBalancerModificationLock.writeLock().unlock();
    }
    
    /**
     * Load balances a REST operation. Automatically creates the needed load balancers, clients, etc. if needed.
     * @param targetService The service to load balance.
     * @param endpointURI The endpoint URI (e.g., "products").
     * @param entityClass The class of entities to send/receive.
     * @param operation The operation to load balance as Java8 lambda.
     * 				E.g.: "client -> CRUDOperations.getEntity(client, id)".
     * @param <R> The expected return type.
     * @param <T> The entity type of the entity to send/receive.
     * @return Returns the return value of the load balanced operation.
     */
    public static <T, R> R loadBalanceRESTOperation(Service targetService,
    		String endpointURI, Class<T> entityClass,
    		Function<RESTClient<T>, R> operation) {
    	return getServiceLoadBalancer(targetService).loadBalanceRESTOperation(endpointURI, entityClass, operation);
	}
    
    private <T, R> R loadBalanceRESTOperation(String endpointURI,
    		Class<T> entityClass, Function<RESTClient<T>, R> operation) {
    	R r = null;
    	if (loadBalancer == null) {
    		System.err.println("Load Balancer was not initialized for service: " + targetService.getServiceName());
    		System.err.println("\tIs Registry up?");
    		updateLoadBalancersForServiceUsingRegistry(targetService);
    	}
    	
    	loadBalancerModificationLock.readLock().lock();
    	if (loadBalancer == null || loadBalancer.getAllServers().isEmpty()) {
    		System.err.println("No Server registered for Service: " + targetService.getServiceName());
    	} else {
    		r = LoadBalancerCommand.<R>builder()
                    .withLoadBalancer(loadBalancer)
                    .withRetryHandler(retryHandler)
                    .build()
                    .submit(server -> Observable.just(
                    		operation.apply(
                    				(RESTClient<T>) getEndpointClientCollection(endpointURI, entityClass)
                    				.getRESTClient(server))))
                    .toBlocking().first();
    	}
		loadBalancerModificationLock.readLock().unlock();
		return r;
	}
    
    /**
     * Sends a multicast to all known instances of the service.
     * Does not repeat failed sends.
     * @param targetService The service to send to.
     * @param endpointURI The endpoint URI (e.g., "cache").
     * @param entityClass The class of entities to send/receive.
     * @param operation The operation to load balance as Java8 lambda.
     * 				E.g.: "client -> CRUDOperations.getEntity(client, id)".
     * @param <R> The expected return type.
     * @param <T> The entity type of the entity to send/receive.
     * @return List of all responses. Contains null for each unreachable server.
     */
    public static <T, R> List<R> multicastRESTOperation(Service targetService,
    		String endpointURI, Class<T> entityClass,
    		Function<RESTClient<T>, R> operation) {
    	return getServiceLoadBalancer(targetService).multicastRESTOperation(endpointURI, entityClass, operation, null);
    }
    
    /**
     * Sends a multicast to all known instances of this service, except for the
     * one actually sending (this instance).
     * Does not repeat failed sends.
     * @param endpointURI The endpoint URI (e.g., "cache").
     * @param entityClass The class of entities to send/receive.
     * @param operation The operation to load balance as Java8 lambda.
     * 				E.g.: "client -> CRUDOperations.getEntity(client, id)".
     * @param <R> The expected return type.
     * @param <T> The entity type of the entity to send/receive.
     * @return List of all responses. Contains null for each unreachable server.
     */
    public static <T, R> List<R> multicastRESTToOtherServiceInstances(String endpointURI, Class<T> entityClass,
    		Function<RESTClient<T>, R> operation) {
    	return getServiceLoadBalancer(RegistryClient.CLIENT.getMyService())
    			.multicastRESTOperation(endpointURI, entityClass, operation,
    					RegistryClient.CLIENT.getMyServiceInstanceServer());
    }
    
    //exception can be null
    private <T, R> List<R> multicastRESTOperation(String endpointURI, Class<T> entityClass,
    		Function<RESTClient<T>, R> operation, Server exception) {
    	loadBalancerModificationLock.readLock().lock();
    	List<Server> servers = new ArrayList<>(loadBalancer.getAllServers());
    	if (exception != null) {
    		servers.remove(exception);
    	}
    	List<R> responses = new ArrayList<>();
    	responses = servers.parallelStream().map(
    		server -> {
    			try {
    				return operation.apply((RESTClient<T>) getEndpointClientCollection(endpointURI, entityClass)
            				.getRESTClient(server));
    			} catch (Exception e) {
    				return null;
    			}
    		}).collect(Collectors.toList());
    	loadBalancerModificationLock.readLock().unlock();
    	return responses;
    }
}
