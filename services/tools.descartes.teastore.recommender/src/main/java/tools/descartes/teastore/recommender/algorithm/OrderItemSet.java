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
package tools.descartes.teastore.recommender.algorithm;

import java.util.HashMap;
import java.util.Map;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

/**
 * Objects of this class holds a mapping of {@link Product} IDs to quantities
 * that were bought in the same {@link Order} by one {@link User}. Non-present
 * {@link Product} IDs imply a quantity of 0.
 * 
 * @author Johannes Grohmann
 *
 */
public class OrderItemSet {

	/**
	 * Standard constructor.
	 */
	public OrderItemSet() {
		orderset = new HashMap<>();
	}

	/**
	 * The user that made this order.
	 */
	private long userId;

	/**
	 * The orderId that the Items were bought in.
	 */
	private long orderId;

	/**
	 * The productIds that were bought together with the given quantity.
	 */
	private Map<Long, Integer> orderset;

	/**
	 * @return the orderset
	 */
	public Map<Long, Integer> getOrderset() {
		return orderset;
	}

	/**
	 * @param orderset
	 *            the orderset to set
	 */
	public void setOrderset(Map<Long, Integer> orderset) {
		this.orderset = orderset;
	}

	/**
	 * @return the orderId
	 */
	public long getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
}
