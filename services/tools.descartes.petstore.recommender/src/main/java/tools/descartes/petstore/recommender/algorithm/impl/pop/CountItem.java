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
package tools.descartes.petstore.recommender.algorithm.impl.pop;

/**
 * Simple class mappping a product id to the number of sells. Optimized for
 * {@link PopularityBasedRecommender}.
 * 
 * @author Johannes Grohmann
 *
 */
public class CountItem implements Comparable<CountItem> {

	/**
	 * The number of sells of this product.
	 */
	private long count;

	/**
	 * The id of this product.
	 */
	private long productId;

	/**
	 * Constructor.
	 * 
	 * @param product
	 *            The id of the product to count.
	 * @param count
	 *            The number of sold products.
	 */
	public CountItem(Long product, Long count) {
		super();
		this.productId = product;
		this.count = count;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * @return the productId
	 */
	public long getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(long productId) {
		this.productId = productId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CountItem o) {
		return Long.compare(this.getCount(), o.getCount());
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
		result = prime * result + (int) (productId ^ (productId >>> 32));
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
		CountItem other = (CountItem) obj;
		if (productId != other.productId) {
			return false;
		}
		return true;
	}

}
