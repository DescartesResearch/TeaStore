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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostRemove;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.entities.OrderItem;

/**
 * Persistence entity Class for OrderItems (item with quantity in shopping cart or order).
 * @author Joakim von Kistowski
 *
 */
@Entity
public class PersistenceOrderItem extends OrderItem {

	@Id
	@GeneratedValue
	private long id;
	
	private int quantity;
	private long unitPriceInCents;

	@ManyToOne(optional = false)
	private PersistenceProduct product;

	@ManyToOne(optional = false)
	private PersistenceOrder order;
	
	/**
	 * Create a new and empty order item.
	 */
	PersistenceOrderItem() {
		super();
	}
	
	/**
	 * Clear products and orders from cache post remove.
	 */
	@PostRemove
	private void clearCaches() {
		CacheManager.MANAGER.clearCache(PersistenceProduct.class);
		CacheManager.MANAGER.clearCache(PersistenceOrder.class);
		CacheManager.MANAGER.clearRemoteCache(PersistenceOrderItem.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	 /**
	 * {@inheritDoc}
	 */
	@Override
	public long getProductId() {
		return product.getId();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductId(long productId) {
		//unsupported
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getUnitPriceInCents() {
		return unitPriceInCents;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUnitPriceInCents(long unitPriceInCents) {
		this.unitPriceInCents = unitPriceInCents;
	}

	/**
	 * Gets the product.
	 * @return the product to get.
	 */
	public PersistenceProduct getProduct() {
		return product;
	}

	/**
	 * Sets the product.
	 * @param product the product to set.
	 */
	void setProduct(PersistenceProduct product) {
		this.product = product;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getOrderId() {
		return getOrder().getId();
	}

	/**
	 * Unsupported operation.
	 * @param orderId unsupported parameter.
	 */
	public void setOrderId(long orderId) {
		//unsupported operation
	}
	
	/**
	 * Gets the order.
	 * @return The order to get.
	 */
	public PersistenceOrder getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 * @param order the order to set.
	 */
	void setOrder(PersistenceOrder order) {
		this.order = order;
	}
	
}
