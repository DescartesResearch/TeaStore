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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostRemove;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.entities.Product;

/**
 * Persistence entity for products.
 * @author Joakim von Kistowski
 *
 */
@Entity
public class PersistenceProduct extends Product {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(length = 100)
	private String name;
	@Lob
	private String description;
	private long listPriceInCents;
	
	@ManyToOne
	private PersistenceCategory category;
	
	@OneToMany(mappedBy = "product", orphanRemoval = true, cascade = {CascadeType.ALL})
	private List<PersistenceOrderItem> orderItems;
	
	/**
	 * Clear categories and order items from cache post remove.
	 */
	@PostRemove
	private void clearCaches() {
		CacheManager.MANAGER.clearCache(PersistenceCategory.class);
		CacheManager.MANAGER.clearRemoteCache(PersistenceProduct.class);
	}
	
	/**
	 * Create a new and empty product.
	 */
	PersistenceProduct() {
		super();
		orderItems = new ArrayList<PersistenceOrderItem>();
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
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getCategoryId() {
		return category.getId();
	}
	
	/**
	 * Unsupported operation. Does nothing.
	 * @param categoryId Unsupported parameter.
	 */
	@Override
	public void setCategoryId(long categoryId) {
		// unsupported operation
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getListPriceInCents() {
		return listPriceInCents;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setListPriceInCents(long listPriceInCents) {
		this.listPriceInCents = listPriceInCents;
	}

	/**
	 * {@inheritDoc}
	 */
	public PersistenceCategory getCategory() {
		return category;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCategory(PersistenceCategory category) {
		this.category = category;
	}

	/**
	 * @return the orderItems
	 */
	public List<PersistenceOrderItem> getOrderItems() {
		return orderItems;
	}
	
}
