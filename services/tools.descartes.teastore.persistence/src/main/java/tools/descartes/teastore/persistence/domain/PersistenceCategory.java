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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PostRemove;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.entities.Category;

/**
 * Entity for persisting Categories in database.
 * @author Joakim von Kistowski
 *
 */
@Entity
public class PersistenceCategory extends Category {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(length = 100)
	private String name;
	@Lob
	private String description;
	
	@OneToMany(mappedBy = "category", orphanRemoval = true, cascade = {CascadeType.ALL})
	private List<PersistenceProduct> products;
	
	/**
	 * Creates a new and empty category.
	 */
	PersistenceCategory() {
		super();
		products = new ArrayList<>();
	}
	
	/**
	 * Clear products from cache to update relationships.
	 */
	@PostRemove
	private void clearCaches() {
		CacheManager.MANAGER.clearCache(PersistenceProduct.class);
		CacheManager.MANAGER.clearRemoteCache(PersistenceCategory.class);
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
	 * Get all products in this category.
	 * @return All products in the category.
	 */
	public List<PersistenceProduct> getProducts() {
		return products;
	}
	
}
