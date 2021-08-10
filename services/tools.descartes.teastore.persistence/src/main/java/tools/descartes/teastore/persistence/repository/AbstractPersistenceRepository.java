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
package tools.descartes.teastore.persistence.repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

/**
 * Abstract Repository for CRUD Operations for persistence Entities.
 * Already provides lookup and delete operations.
 * @author Joakim von Kistowski
 *
 * @param <E> The class type of the Interface entity
 * 		(i.e., the entity type that is parsed and sent out to clients using REST).
 * @param <P> The class type of the Persistence entity
 * 		(i.e., the entity that is stored in the database).
 */
public abstract class AbstractPersistenceRepository<E, P extends E> {
	
	/**
	 * Gets the ID of a persistence entity.
	 * @param p The persistence entity for which the ID is to be retreived.
	 * @return The id of persistence entity p.
	 */
	protected abstract long getId(P p);
	
	/**
	 * Get the Java Class of the persistence entity objects for this repository.
	 * @return The Java Class of the persistence entity objects.
	 */
	protected abstract Class<P> getEntityClass();
	
	/**
	 * Get the entity manager factory of the persistence context.
	 * @return The entity manager factory.
	 */
	public EntityManagerFactory getEMF() {
		return EMFManager.getEMF();
	}
	
	/**
	 * Creates a new entity manager and returns it.
	 * Don't forget to close!
	 * @return A new entity manager.
	 */
	protected EntityManager getEM() {
		return getEMF().createEntityManager();
	}
	
	/**
	 * Create a new persistence entity from an interface entity
	 * (usually received via REST).
	 * @param entity The entity template for the entity to create.
	 * @return The new ID of the newly created entity.
	 */
	public abstract long createEntity(E entity);
	
	/**
	 * Updates the entity with the values from the provided interface
	 * entity (usually received via REST).
	 * @param id The id of the entity to update (ignore the id in the passed entity).
	 * @param entity The values of the entity to update. Ignore the id of the entity.
	 * 		You may also choose to ignore additional values, if you do not support updating them.
	 * @return True, if update succeded. False otherwise.
	 */
	public abstract boolean updateEntity(long id, E entity);
	
	/**
	 * Retrieve the entity with the given ID.
	 * @param id ID of the entity to retrieve.
	 * @return The entity. Null, if none was found.
	 */
	public P getEntity(long id) {
		P instance = null;
		EntityManager em = getEM();
	    try {
	        instance = em.find(getEntityClass(), id);
	    } finally {
	        em.close();
	    }
		return instance;
	}
	
	/**
	 * Get all entities of the generic types of entities managed in this repository.
	 * @return All entities in a list.
	 */
	public List<P> getAllEntities() {
		return getAllEntities(-1, -1);
	}
	
	/**
	 * Get all entities of the generic types of entities managed in this repository.
	 * Starts with the "start" entity and returns at maximum "limit" entities.
	 * @param start The index of the entity to start with. Set to negative value to start at the beginning.
	 * @param limit The maximum number of entites to return. Set to negative value to return all.
	 * @return List of entities.
	 */
	public List<P> getAllEntities(int start, int limit) {
		EntityManager em = getEM();
		List<P> entities = null;
		try {
			TypedQuery<P> allMatchesQuery =
					em.createQuery("SELECT u FROM " + getEntityClass().getName() + " u", getEntityClass());
			if (start >= 0) {
				allMatchesQuery = allMatchesQuery.setFirstResult(start);
		    }
			if (limit >= 0) {
				allMatchesQuery = allMatchesQuery.setMaxResults(limit);
			}
			entities = allMatchesQuery.getResultList();
		} finally {
			em.close();
		}
		if (entities == null) {
			entities = new ArrayList<P>();
		}
		return entities;
	}
	
	/**
	 * Removes the entity with the id from database.
	 * @param id The id of the entity to remove.
	 * @return True, if delete succeded. False, if it failed (entity with id not found).
	 */
	public boolean removeEntity(long id) {
		boolean found = false;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        P entity = em.find(getEntityClass(), id);
	        if (entity != null) {
	        	em.remove(entity);
	        	found = true;
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
	    return found;
	}
	
	/**
	 * Returns the query result for a query with a start and limit parameter.
	 * Negative starts and limits are ignored.
	 * @param em The currently open entity manager.
	 * @param query The query. Parameters must all be bound.
	 * @param start The start index. Negative values are ignored.
	 * @param limit The limit. Negative values are ignored.
	 * @return The query result.
	 */
	protected List<P> resultsWithStartAndLimit(EntityManager em, TypedQuery<P> query, int start, int limit) {
		if (start >= 0) {
			query.setFirstResult(start);
	    }
		if (limit >= 0) {
			query = query.setMaxResults(limit);
		}
		return query.getResultList();
	}
}
