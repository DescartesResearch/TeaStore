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
package tools.descartes.teastore.persistence.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import tools.descartes.teastore.persistence.repository.AbstractPersistenceRepository;
import tools.descartes.teastore.entities.Product;

/**
 * Repository that performs transactional CRUD operations for Products on database.
 * @author Joakim von Kistowski
 *
 */
public final class ProductRepository extends AbstractPersistenceRepository<Product, PersistenceProduct> {

	/**
	 * Singleton for the ProductRepository.
	 */
	public static final ProductRepository REPOSITORY = new ProductRepository();
	
	//Private constructor.
	private ProductRepository() {
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long createEntity(Product entity) {
		PersistenceProduct product = new PersistenceProduct();
		product.setName(entity.getName());
		product.setDescription(entity.getDescription());
		product.setListPriceInCents(entity.getListPriceInCents());
		
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceCategory cat = em.find(PersistenceCategory.class, entity.getCategoryId());
	        if (cat != null) {
	        	product.setCategory(cat);
	        	em.persist(product);
	        } else {
	        	product.setId(-1L);
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
	    return product.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateEntity(long id, Product entity) {
		boolean found = false;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceProduct product = em.find(getEntityClass(), id);
	        if (product != null) {
	        	product.setName(entity.getName());
	        	product.setDescription(entity.getDescription());
	        	product.setListPriceInCents(entity.getListPriceInCents());
	        	found = true;
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
	    return found;
	}
	
	/**
	 * Gets all Products in the Category of the given categoryId.
	 * @param categoryId The id of the Category containing the Products.
	 * @param start The index of the first Product to return. Negative value to start at the beginning.
	 * @param limit The maximum number of Products to return. Negative value to return all.
	 * @return List of Products with the specified Category.
	 */
	public List<PersistenceProduct> getAllEntities(long categoryId, int start, int limit) {
		List<PersistenceProduct> entities = null;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceCategory cat = em.find(PersistenceCategory.class, categoryId);
	        if (cat != null) {
	        	TypedQuery<PersistenceProduct> allMatchesQuery =
	        			em.createQuery("SELECT u FROM " + getEntityClass().getName()
	        					+ " u WHERE u.category = :cat", getEntityClass());
	        	allMatchesQuery.setParameter("cat", cat);
	        	entities = resultsWithStartAndLimit(em, allMatchesQuery, start, limit);
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
		if (entities == null) {
			return new ArrayList<PersistenceProduct>();
		}
		return entities;
	}

	/**
	 * Gets the count of all Products in the Category of the given categoryId.
	 * @param categoryId The id of the Category containing the Products.
	 * @return Count of Products with the specified Category.
	 */
	public long getProductCount(long categoryId) {
		long count = -1;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceCategory cat = em.find(PersistenceCategory.class, categoryId);
	        if (cat != null) {
	        	TypedQuery<Long> allMatchesQuery =
	        			em.createQuery("SELECT COUNT(u) FROM " + getEntityClass().getName()
	        					+ " u WHERE u.category = :cat", Long.class);
	        	allMatchesQuery.setParameter("cat", cat);
	        	Long countResult = allMatchesQuery.getSingleResult();
	        	if (countResult != null) {
	        		count = countResult;
	        	}
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
		return count;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getId(PersistenceProduct v) {
		return v.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<PersistenceProduct> getEntityClass() {
		return PersistenceProduct.class;
	}
	
}
