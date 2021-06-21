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

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.registryclient.util.TimeoutException;

/**
 * Default REST operations that transfer Entities to/from a service that has a
 * standard conforming REST-API. Do not utilize any load balancers. Use the
 * LoadBalancedCRUDOperations instead for all normal use cases.
 * 
 * @author Joakim von Kistowski
 */
public final class NonBalancedCRUDOperations {
  private static final Logger LOG = LoggerFactory.getLogger(NonBalancedCRUDOperations.class);

  private NonBalancedCRUDOperations() {

  }

  /**
   * Sends an Entity to be created "as new" by the receiving service.
   * 
   * @param entity
   *          The new entity to create. ID may remain unset, it will be ignored by
   *          target service.
   * @param client
   *          The REST client to use.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return The new ID of the created entity. Target service creates a new ID,
   *         any passed ID is ignored. Returns -1L if creation failed. Returns 0
   *         if creation worked, but ID remains unkown.
   */
  public static <T> long sendEntityForCreation(RESTClient<T> client, T entity)
      throws NotFoundException, TimeoutException {
    Response response = ResponseWrapper.wrap(HttpWrapper.wrap(client.getEndpointTarget())
        .post(Entity.entity(entity, MediaType.APPLICATION_JSON), Response.class));
    long id = -1L;
    // If resource was created successfully
    if (response != null && response.getStatus() == 201) {
      id = 0L;
      // check if response an Id; if yes: return the id
      try {
        id = response.readEntity(Long.class);
      } catch (ProcessingException e) {
        LOG.warn("Response did not conform to expected message type. Expected a Long ID.");
      }
    } else if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return id;
  }

  /**
   * Sends an Entity to be updated using the values of the provided entity. Note
   * that not all values may be used by the receiving service. The values used
   * depend on which changes are allowed in the domain model.
   * 
   * @param id
   *          The id of the entity to update. Ids stored within the entity are
   *          ignored.
   * @param entity
   *          The entity to be updated. Entity is matched using its ID.
   * @param client
   *          The REST client to use.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return True, if update succeeded. False, otherwise.
   */
  public static <T> boolean sendEntityForUpdate(RESTClient<T> client, long id, T entity)
      throws NotFoundException, TimeoutException {
    Response response = ResponseWrapper
        .wrap(HttpWrapper.wrap(client.getEndpointTarget().path(String.valueOf(id)))
            .put(Entity.entity(entity, MediaType.APPLICATION_JSON), Response.class));
    if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    if (response != null && response.getStatus() == 200) {
      return true;
    }
    return false;
  }

  /**
   * Deletes the entity at the target id.
   * 
   * @param id
   *          The ID of the entity to delete.
   * @param client
   *          The REST client to use.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return True, if deletion succeeded; false otherwise.
   */
  public static <T> boolean deleteEntity(RESTClient<T> client, long id)
      throws NotFoundException, TimeoutException {
    Response response = ResponseWrapper
        .wrap(HttpWrapper.wrap(client.getEndpointTarget().path(String.valueOf(id))).delete());

    if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == 200) {
      return true;
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return false;
  }

  /**
   * Returns the entity with the specified id. Returns null if it does not exist.
   * 
   * @param id
   *          Id of the entity to find.
   * @param client
   *          The REST client to use.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return The entity; null if it does not exist.
   */
  public static <T> T getEntity(RESTClient<T> client, long id)
      throws NotFoundException, TimeoutException {
    Response response = ResponseWrapper
        .wrap(HttpWrapper.wrap(client.getEndpointTarget().path(String.valueOf(id))).get());
    T entity = null;
    if (response != null && response.getStatus() < 400) {
      try {
        entity = response.readEntity(client.getEntityClass());
      } catch (NullPointerException | ProcessingException e) {
        LOG.warn("Response did not conform to expected entity type.");
      }
    } else if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return entity;
  }

  /**
   * Returns a list of Entities of the relevant type.
   * 
   * @param client
   *          The REST client to use.
   * @param startIndex
   *          The index of the first entity to return (index, not ID!). -1, if you
   *          don't want to set an index.
   * @param limit
   *          Maximum amount of entities to return. -1, for no max.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return List of entities; empty list if non were found.
   */
  public static <T> List<T> getEntities(RESTClient<T> client, int startIndex, int limit)
      throws NotFoundException, TimeoutException {
    WebTarget target = client.getEndpointTarget();
    if (startIndex >= 0) {
      target = target.queryParam("start", startIndex);
    }
    if (limit >= 0) {
      target = target.queryParam("max", limit);
    }

    GenericType<List<T>> listType = client.getGenericListType();
    Response response = ResponseWrapper.wrap(HttpWrapper.wrap(target).get());
    List<T> entities = new ArrayList<T>();
    
    if (response != null && response.getStatus() == 200) {
      try {
        entities = response.readEntity(listType);
      } catch (ProcessingException e) {
        LOG.warn("Response did not conform to expected entity type. List expected.");
      }
    } else if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return entities;
  }

  /**
   * Returns a list of Entities of the relevant type after filtering using a path
   * param query. Example: "category", 2, 1, 3 will return 3 items in Category
   * with ID 2, beginning from item with index 1 (skipping item 0). Note that the
   * AbstractCRUDEndpoint does not offer this feature by default.
   * 
   * @param client
   *          The REST client to use.
   * @param filterURI
   *          Name of the objects to filter for. E.g., "category".
   * @param filterId
   *          Id of the Object to filter for. E.g, 2
   * @param startIndex
   *          The index of the first entity to return (index, not ID!). -1, if you
   *          don't want to set an index.
   * @param limit
   *          Maximum amount of entities to return. -1, for no max.
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return List of entities; empty list if non were found.
   */
  public static <T> List<T> getEntities(RESTClient<T> client, String filterURI, long filterId,
      int startIndex, int limit) throws NotFoundException, TimeoutException {
    WebTarget target = client.getEndpointTarget().path(filterURI).path(String.valueOf(filterId));
    if (startIndex >= 0) {
      target = target.queryParam("start", startIndex);
    }
    if (limit >= 0) {
      target = target.queryParam("max", limit);
    }
    Response response = ResponseWrapper.wrap(HttpWrapper.wrap(target).get());
    List<T> entities = new ArrayList<T>();
    if (response != null && response.getStatus() == 200) {
      try {
        entities = response.readEntity(client.getGenericListType());
      } catch (ProcessingException e) {
        e.printStackTrace();
        LOG.warn("Response did not conform to expected entity type. List expected.");
      }
    } else if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return entities;
  }

  /**
   * Returns an Entity of the relevant type by using a unique non-primary-key
   * property. Example: Get user with user name. Note that the
   * AbstractCRUDEndpoint does not offer this feature by default.
   * 
   * @param client
   *          The REST client to use.
   * @param propertyURI
   *          Name of the property. E.g., "name".
   * @param propertyValue
   *          Value of the property, e.g., "user1".
   * @param <T>
   *          Type of entity to handle.
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws TimeoutException
   *           If 408 was returned.
   * @return The entity; null if it does not exist.
   */
  public static <T> T getEntityWithProperty(RESTClient<T> client, String propertyURI,
      String propertyValue) throws NotFoundException, TimeoutException {
    WebTarget target = client.getEndpointTarget().path(propertyURI).path(propertyValue);
    Response response = ResponseWrapper.wrap(HttpWrapper.wrap(target).get());
    T entity = null;
    if (response != null && response.getStatus() < 400) {
      try {
        entity = response.readEntity(client.getEntityClass());
      } catch (NullPointerException | ProcessingException e) {
        // This happens if no entity was found
      }
    } else if (response != null) {
      response.bufferEntity();
    }
    if (response != null && response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
      throw new NotFoundException();
    } else if (response != null && response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
      throw new TimeoutException();
    }
    return entity;
  }

}
