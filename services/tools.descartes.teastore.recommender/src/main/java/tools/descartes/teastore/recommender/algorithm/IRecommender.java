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

import java.util.List;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

/**
 * Interface providing the recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public interface IRecommender {

	/**
	 * Trains this recommender with the given list of historical {@link OrderItems}s
	 * and {@link Order}s. This list is used as knowledge basis for the recommending
	 * in {@link IRecommender#recommendProducts(List)}.<br>
	 * The mapping of the different {@link OrderItem}s to their corresponding
	 * {@link Order}s is done via {@link OrderItem#getOrderId()}.<br>
	 * Calling this method twice will trigger a retraining.
	 * 
	 * @param orderItems
	 *            A list of {@link OrderItem}s that were placed by users.
	 * @param orders
	 *            A list of {@link Order}s that were placed by users.
	 */
	public void train(List<OrderItem> orderItems, List<Order> orders);

	/**
	 * Return a list of all {@link Product}s, which are recommended for the given
	 * {@link User} buying the given list of {@link OrderItem}s. <br>
	 * {@link IRecommender#train(List)} must be called before any recommendations
	 * can be executed.
	 * 
	 * @param currentItems
	 *            A list containing all {@link OrderItem}s in the current cart.
	 *            Might be empty.
	 * @param userid
	 *            The id of the {@link User} to recommend for. May be null.
	 * @return List of all IDs of the {@link Product} entities that are recommended
	 *         to add to the cart. Does not contain any {@link Product} that is
	 *         already part of the given list of {@link OrderItem}s. Might be empty.
	 * 
	 * @throws UnsupportedOperationException
	 *             If this instance is not ready to recommend, i.e.,
	 *             {@link IRecommender#train(List)} has not been invoked or
	 *             terminated yet.
	 */
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems) throws UnsupportedOperationException;

}
