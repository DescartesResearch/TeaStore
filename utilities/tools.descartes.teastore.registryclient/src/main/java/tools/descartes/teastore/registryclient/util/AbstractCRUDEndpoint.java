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

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;

/**
 * Abstract endpoint for providing entities as resources using REST.
 * @author Joakim von Kistowski
 *
 * @param <T> The entity type.
 */
@Path("abstract") //This is overwritten by inheritance
@Produces({ "application/json" })
@Consumes({ "application/json" })
public abstract class AbstractCRUDEndpoint<T> {
	
	/**
	 * Create a new entity by copying the passed entity. Any passed IDs are always ignored.
	 * A new ID will be created.
	 * @param entity Template for the new entity.
	 * @return A new entity with the initial values of the passed template.
	 */
	@POST
	public Response create(final T entity) {
		long id = createEntity(entity);
		return Response.created(UriBuilder.fromResource(AbstractCRUDEndpoint.class).
				path(String.valueOf(id)).build()).entity(id).build();
	}
	
	/**
	 * Create a new entity (usually by passing this on to persistence).
	 * Copy the values of all Attributes of entity to the new Entity, EXEPT the ID.
	 * Create a new ID, write it to the new entity and return it.
	 * @param entity Entity to copy and create as new with a new ID.
	 * @return The new ID. Also write this new ID to the new entity.
	 */
	protected abstract long createEntity(final T entity);

	/**
	 * Retreive and entity with the provided ID.
	 * @param id ID of the entity to find.
	 * @return A Response containing the entity.
	 */
	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") final Long id) {
		if (id == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		T entity = findEntityById(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}
	
	/**
	 * Find the entity with the given ID and return it. Return null if none was found.
	 * @param id The ID of the Entity to find.
	 * @return The retreived Entity. null, if no entity was found.
	 */
	protected abstract T findEntityById(final long id);

	/**
	 * Return a list of all entities starting at the startIndex_th entity.
	 * Return maxResultCount entities or fewer if fewer exist.
	 * Negative or 0 startIndexes should result in the startIndex being ignored.
	 * @param startPosition The index to start. Negative or null startIndex starts returning at the beginning.
	 * @param maxResult Max amount of entities to return. Negative or null maxResultCount: return all.
	 * @return List of all entities within the provided range. Returns an empty list for no matches
	 */
	@GET
	public List<T> listAll(@QueryParam("start") final Integer startPosition,
			@QueryParam("max") final Integer maxResult) {
		final List<T> entities = listAllEntities(parseIntQueryParam(startPosition), parseIntQueryParam(maxResult));
		return entities;
	}

	/**
	 * Return a list of all entities starting at the startIndex_th entity.
	 * Return maxResultCount entities or fewer if fewer exist.
	 * Negative or 0 startIndexes should result in the startIndex being ignored.
	 * Negative maxResultCounts should result in all results from startIndex being returned.
	 * @param startIndex The index to start. Negative startIndex starts returning at the beginning.
	 * @param maxResultCount Max amount of entities to return. Negative maxResultCount: return all.
	 * @return List of all entities within the provided range. Return an empty list for no matches.
	 */
	protected abstract List<T> listAllEntities(final int startIndex, final int maxResultCount);
	
	/**
	 * Update the entity with ID id with the attributes of the passed entity.
	 * You are free to choose for which attributes you allow updates and which attributes to ignore.
	 * Will always ignore the ID in the passed entity and use the separate ID instead.
	 * @param id The id of the entity to update.
	 * @param entity The values of the entity to update.
	 * @return Status Code 200 if update succeeded, 404 if it didn't.
	 */
	@PUT
	@Path("/{id:[0-9][0-9]*}")
	public Response update(@PathParam("id") Long id, final T entity) {
		boolean updated = false;
		if (id != null && entity != null) {
			updated = updateEntity(id, entity);
		}
		if (updated) {
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	/**
	 * Update the entity with ID id with the attributes of the passed entity.
	 * You are free to choose for which attributes you allow updates and which attributes to ignore.
	 * Note the the ID passed in the entity itself must ALWAYS be ignored.
	 * @param id The id of the entity to update.
	 * @param entity The values of the entity to update.
	 * @return True, if a matching entity was found and updated. False, if the update failed.
	 */
	protected abstract boolean updateEntity(long id, final T entity);

	/**
	 * Delete the entity with ID id.
	 * @param id The id of the entity to delete.
	 * @return Status Code 200 if delete succeeded, 404 if it didn't.
	 */
	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") final Long id) {
		boolean deleted = deleteEntity(id);
		if (deleted) {
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	/**
	 * Delete the entity with ID id.
	 * @param id The id of the entity to delete.
	 * @return True, if a matching entity was found and deleted. False, if no entity was found or if no delte occured.
	 */
	protected abstract boolean deleteEntity(long id);
	
	/**
	 * Parses an int query param and catches errors. Returns -1 on errors or missing params.
	 * @param queryArg The query param to parse.
	 * @return -1 on errors. The query param otherwise.
	 */
	protected int parseIntQueryParam(Integer queryArg) {
		if (queryArg != null) {
			return queryArg;
		}
		return -1;
	}
}
