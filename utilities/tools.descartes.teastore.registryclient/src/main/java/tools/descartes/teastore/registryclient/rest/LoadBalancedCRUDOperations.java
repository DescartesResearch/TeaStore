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
package tools.descartes.teastore.registryclient.rest;

import java.util.List;
import java.util.Optional;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.util.NotFoundException;

/**
 * Default REST operations that transfer Entities to/from a service that has a
 * standard conforming REST-API. Uses load balancing to find the target
 * services.
 * 
 * @author Joakim von Kistowski
 */
public final class LoadBalancedCRUDOperations {

	private LoadBalancedCRUDOperations() {

	}

	/**
	 * Sends an Entity to be created "as new" by the receiving service.
	 * 
	 * @param entity
	 *            The new entity to create. ID may remain unset, it will be ignored
	 *            by target service.
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return The new ID of the created entity. Target service creates a new ID,
	 *         any passed ID is ignored. Returns -1L if creation failed. Returns 0
	 *         if creation worked, but ID remains unkown.
	 */
	public static <T> long sendEntityForCreation(Service service, String endpointURI, Class<T> entityClass, T entity)
			throws NotFoundException, LoadBalancerTimeoutException {
		return Optional.ofNullable(ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.sendEntityForCreation(client, entity))).orElse(-1L);
	}

	/**
	 * Sends an Entity to be updated using the values of the provided entity. Note
	 * that not all values may be used by the receiving service. The values used
	 * depend on which changes are allowed in the domain model.
	 * 
	 * @param id
	 *            The id of the entity to update. Ids stored within the entity are
	 *            ignored.
	 * @param entity
	 *            The entity to be updated. Entity is matched using its ID.
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return True, if update succeeded. False, otherwise.
	 */
	public static <T> boolean sendEntityForUpdate(Service service, String endpointURI, Class<T> entityClass, long id,
			T entity) throws NotFoundException, LoadBalancerTimeoutException {
		return Optional.ofNullable(ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.sendEntityForUpdate(client, id, entity))).orElse(false);
	}

	/**
	 * Deletes the entity at the target id.
	 * 
	 * @param id
	 *            The ID of the entity to delete.
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return True, if deletion succeeded; false otherwise.
	 */
	public static <T> boolean deleteEntity(Service service, String endpointURI, Class<T> entityClass, long id)
			throws NotFoundException, LoadBalancerTimeoutException {
		return Optional.ofNullable(ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.deleteEntity(client, id))).orElse(false);
	}

	/**
	 * Returns the entity with the specified id. Returns null if it does not exist.
	 * 
	 * @param id
	 *            Id of the entity to find.
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return The entity; null if it does not exist.
	 */
	public static <T> T getEntity(Service service, String endpointURI, Class<T> entityClass, long id)
			throws NotFoundException, LoadBalancerTimeoutException {
		return ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.getEntity(client, id));
	}

	/**
	 * Returns a list of Entities of the relevant type.
	 * 
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param propertyName
	 *            name of filter property
	 * @param propertyValue
	 *            value of filter property
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return List of entities; empty list if non were found.
	 */
	public static <T> T getEntityWithProperties(Service service, String endpointURI, Class<T> entityClass,
			String propertyName, String propertyValue) throws NotFoundException, LoadBalancerTimeoutException {
		return ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.getEntityWithProperty(client, propertyName, propertyValue));
	}

	/**
	 * Returns a list of Entities of the relevant type.
	 * 
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param startIndex
	 *            The index of the first entity to return (index, not ID!). -1, if
	 *            you don't want to set an index.
	 * @param limit
	 *            Maximum amount of entities to return. -1, for no max.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return List of entities; empty list if non were found.
	 */
	public static <T> List<T> getEntities(Service service, String endpointURI, Class<T> entityClass, int startIndex,
			int limit) throws NotFoundException, LoadBalancerTimeoutException {
		return ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.getEntities(client, startIndex, limit));
	}

	/**
	 * Returns a list of Entities of the relevant type after filtering using a path
	 * param query. Example: "category", 2, 1, 3 will return 3 items in Category
	 * with ID 2, beginning from item with index 1 (skipping item 0). Note that the
	 * AbstractCRUDEndpoint does not offer this feature by default.
	 * 
	 * @param service
	 *            The service to load balance.
	 * @param endpointURI
	 *            The endpoint URI (e.g., "products").
	 * @param entityClass
	 *            The class of entities to send/receive.
	 * @param filterURI
	 *            Name of the objects to filter for. E.g., "category".
	 * @param filterId
	 *            Id of the Object to filter for. E.g, 2
	 * @param startIndex
	 *            The index of the first entity to return (index, not ID!). -1, if
	 *            you don't want to set an index.
	 * @param limit
	 *            Maximum amount of entities to return. -1, for no max.
	 * @param <T>
	 *            Type of entity to handle.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return List of entities; empty list if non were found.
	 */
	public static <T> List<T> getEntities(Service service, String endpointURI, Class<T> entityClass, String filterURI,
			long filterId, int startIndex, int limit) throws NotFoundException, LoadBalancerTimeoutException {
		return ServiceLoadBalancer.loadBalanceRESTOperation(service, endpointURI, entityClass,
				client -> NonBalancedCRUDOperations.getEntities(client, filterURI, filterId, startIndex, limit));
	}

}
