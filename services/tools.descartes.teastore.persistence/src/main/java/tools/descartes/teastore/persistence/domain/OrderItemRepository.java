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
import tools.descartes.teastore.entities.OrderItem;

/**
 * Repository that performs transactional CRUD operations for order items on database.
 * @author Joakim von Kistowski
 *
 */
public final class OrderItemRepository extends AbstractPersistenceRepository<OrderItem, PersistenceOrderItem> {

	/**
	 * Singleton for the ProductRepository.
	 */
	public static final OrderItemRepository REPOSITORY = new OrderItemRepository();
	
	//Private constructor.
	private OrderItemRepository() {
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long createEntity(OrderItem entity) {
		PersistenceOrderItem item = new PersistenceOrderItem();
		item.setQuantity(entity.getQuantity());
		item.setUnitPriceInCents(entity.getUnitPriceInCents());
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceProduct prod = em.find(PersistenceProduct.class, entity.getProductId());
	        PersistenceOrder order = em.find(PersistenceOrder.class, entity.getOrderId());
	        if (prod != null && order != null) {
	        	item.setProduct(prod);
	        	item.setOrder(order);
	        	em.persist(item);
	        } else {
	        	item.setId(-1L);
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
	    return item.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateEntity(long id, OrderItem entity) {
		boolean found = false;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceOrderItem item = em.find(getEntityClass(), id);
	        if (item != null) {
	        	item.setQuantity(entity.getQuantity());
	    		item.setUnitPriceInCents(entity.getUnitPriceInCents());
	        	found = true;
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
	    return found;
	}
	
	/**
	 * Gets all order items for the given productId.
	 * @param productId The id of the product ordered.
	 * @param start The index of the first orderItem to return. Negative value to start at the beginning.
	 * @param limit The maximum number of orderItem to return. Negative value to return all.
	 * @return List of order items with the specified product.
	 */
	public List<PersistenceOrderItem> getAllEntitiesWithProduct(long productId, int start, int limit) {
		List<PersistenceOrderItem> entities = null;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceProduct prod = em.find(PersistenceProduct.class, productId);
	        if (prod != null) {
	        	TypedQuery<PersistenceOrderItem> allMatchesQuery =
	        			em.createQuery("SELECT u FROM " + getEntityClass().getName()
	        					+ " u WHERE u.product = :prod", getEntityClass());
	        	allMatchesQuery.setParameter("prod", prod);
	    		entities = resultsWithStartAndLimit(em, allMatchesQuery, start, limit);
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
		if (entities == null) {
			return new ArrayList<PersistenceOrderItem>();
		}
		return entities;
	}

	/**
	 * Gets all order items in the specified order.
	 * @param orderId The id of the order.
	 * @param start The index of the first orderItem to return. Negative value to start at the beginning.
	 * @param limit The maximum number of orderItem to return. Negative value to return all.
	 * @return List of order items in the specified order.
	 */
	public List<PersistenceOrderItem> getAllEntitiesWithOrder(long orderId, int start, int limit) {
		List<PersistenceOrderItem> entities = null;
		EntityManager em = getEM();
	    try {
	        em.getTransaction().begin();
	        PersistenceOrder order = em.find(PersistenceOrder.class, orderId);
	        if (order != null) {
	        	TypedQuery<PersistenceOrderItem> allMatchesQuery =
	        			em.createQuery("SELECT u FROM " + getEntityClass().getName()
	        					+ " u WHERE u.order = :order", getEntityClass());
	        	allMatchesQuery.setParameter("order", order);
	        	entities = resultsWithStartAndLimit(em, allMatchesQuery, start, limit);
	        }
	        em.getTransaction().commit();
	    } finally {
	        em.close();
	    }
		if (entities == null) {
			return new ArrayList<PersistenceOrderItem>();
		}
		return entities;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getId(PersistenceOrderItem v) {
		return v.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<PersistenceOrderItem> getEntityClass() {
		return PersistenceOrderItem.class;
	}
	
}
