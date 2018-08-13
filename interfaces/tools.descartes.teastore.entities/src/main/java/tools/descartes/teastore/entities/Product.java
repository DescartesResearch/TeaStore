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
 * Entity for Products.
 * 
 * @author Joakim von Kistowski
 *
 */
public class Product {

  private long id;

  private long categoryId;
  private String name;
  private String description;
  private long listPriceInCents;

  /**
   * Create a new and empty Product.
   */
  public Product() {
    // always use the setters when setting variables here
    // do not access the attributes directly
  }

  /**
   * Every Entity needs a Copy-Constructor!
   * 
   * @param product
   *          The entity to Copy.
   */
  public Product(Product product) {
    setId(product.getId());
    setCategoryId(product.getCategoryId());
    setName(product.getName());
    setDescription(product.getDescription());
    setListPriceInCents(product.getListPriceInCents());
  }

  /**
   * Get the id (remember that this ID may be incorrect, especially if a separate id was passed).
   * 
   * @return The id.
   */
  public long getId() {
    return id;
  }

  /**
   * For REST use only. Sets the product ID. Ignored by persistence.
   * 
   * @param id
   *          Product ID, as passed by the REST API.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Set the ID of the product's category.
   * 
   * @return The category id.
   */
  public long getCategoryId() {
    return categoryId;
  }

  /**
   * For REST use only. Sets the category ID. Ignored by persistence.
   * 
   * @param categoryId
   *          Category ID, as passed by the REST API.
   */
  public void setCategoryId(long categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Get the name.
   * 
   * @return The product name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name.
   * 
   * @param name
   *          The product name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the description.
   * 
   * @return The product description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description.
   * 
   * @param description
   *          The product description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the product list price (recommended price per unit) in cents.
   * 
   * @return The list price in cents.
   */
  public long getListPriceInCents() {
    return listPriceInCents;
  }

  /**
   * For REST use only. Sets the product price in cents. Ignored by persistence.
   * 
   * @param listPriceInCents
   *          The price in cents, as passed by the REST API.
   */
  public void setListPriceInCents(long listPriceInCents) {
    this.listPriceInCents = listPriceInCents;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * 
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
    Product other = (Product) obj;
    if (categoryId != other.categoryId) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    return true;
  }

}
