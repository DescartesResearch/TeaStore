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
package tools.descartes.teastore.entities;

/**
 * Entity for Category.
 * @author Joakim von Kistowski
 *
 */
public class Category {

	private long id;

	private String name;
	private String description;
	
	/**
	 * Create a new and empty category.
	 */
	public Category() {
		//always use the setters when setting variables here
		//do not access the attributes directly
	}
	
	/**
	 * Every Entity needs a Copy-Constructor!
	 * @param category The entity to Copy.
	 */
	public Category(Category category) {
		setId(category.getId());
		setName(category.getName());
		setDescription(category.getDescription());
	}
	
	/**
	 * Get the id (remember that this ID may be incorrect, especially if a separate id was passed).
	 * @return The id.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * For REST use only.
	 * Sets the ID. Ignored by persistence.
	 * @param id ID, as passed by the REST API.
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Get the name.
	 * @return The category name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name.
	 * @param name The category name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the description.
	 * @return The category description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description.
	 * @param description The category description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
    }
		Category other = (Category) obj;
		if (id != other.id) {
			return false;
    }
		return true;
  }

}
