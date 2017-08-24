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
package tools.descartes.petsupplystore.recommender.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.recommender.IRecommender;

/**
 * Abstract class for basic recommendation functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public abstract class AbstractRecommender implements IRecommender {

	private boolean trainingFinished = false;

	/**
	 * Defines the maximum number of recommendations different implementations
	 * should return. Is NOT mandatory for any of the algorithms.
	 */
	public static final int MAX_NUMBER_OF_RECOMMENDATIONS = 10;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRecommender.class);

	/**
	 * This set maps a userId to a set, containing the corresponding OrderItemSets,
	 * i.e. all orders that were done by the user.
	 */
	private Map<Long, Set<OrderItemSet>> userItemSets;

	@Override
	public void train(List<OrderItem> orderItems, List<Order> orders) {
		long tic = System.currentTimeMillis();
		// first create order mapping unorderized
		Map<Long, OrderItemSet> unOrderizeditemSets = new HashMap<>();
		for (OrderItem orderItem : orderItems) {
			if (!unOrderizeditemSets.containsKey(orderItem.getOrderId())) {
				unOrderizeditemSets.put(orderItem.getOrderId(), new OrderItemSet());
				unOrderizeditemSets.get(orderItem.getOrderId()).setOrderId(orderItem.getOrderId());
			}
			unOrderizeditemSets.get(orderItem.getOrderId()).getOrderset().add(orderItem.getProductId());
		}
		// now map each id with the corresponding order
		Map<Order, OrderItemSet> itemSets = new HashMap<>();
		for (Long orderid : unOrderizeditemSets.keySet()) {
			Order realOrder = findOrder(orders, orderid);
			itemSets.put(realOrder, unOrderizeditemSets.get(orderid));
		}
		userItemSets = new HashMap<>();
		for (Order order : itemSets.keySet()) {
			if (!userItemSets.containsKey(order.getUserId())) {
				userItemSets.put(order.getUserId(), new HashSet<OrderItemSet>());
			}
			userItemSets.get(order.getUserId()).add(itemSets.get(order));
		}
		executePreprocessing();
		LOG.info("Training recommender finished. Training took: " + (System.currentTimeMillis() - tic) + "ms.");
		trainingFinished = true;
	}

	/**
	 * Triggers implementing classes if they want to execute a pre-processing step
	 * during {@link AbstractRecommender#train(List, List)}.
	 */
	protected void executePreprocessing() {
		// do nothing
	}

	@Override
	public List<Long> recommendProducts(List<OrderItem> currentItems) throws UnsupportedOperationException {
		if (!trainingFinished) {
			throw new UnsupportedOperationException("This instance is not fully trained yet.");
		}
		if (currentItems.isEmpty()) {
			// if input is empty return empty list
			return new LinkedList<>();
		}
		List<Long> items = new ArrayList<>(currentItems.size());
		for (OrderItem item : currentItems) {
			items.add(item.getProductId());
		}
		return execute(items);
	}

	/**
	 * Has to be implemented by subclasses in order to perform actual
	 * recommendation.
	 * 
	 * @param currentItems
	 *            A list containing all ids of {@link OrderItem}s.
	 * @return List of all IDs of the {@link Product} entities that are recommended
	 *         to add to the cart. Does not contain any {@link Product} that is
	 *         already part of the given list of {@link OrderItem}s. Might be empty.
	 */
	protected abstract List<Long> execute(List<Long> currentItems);

	private Order findOrder(List<Order> orders, long orderid) {
		for (Order order : orders) {
			if (order.getId() == orderid) {
				return order;
			}
		}
		return null;
	}

	/**
	 * @return the userItemSets
	 */
	public Map<Long, Set<OrderItemSet>> getUserItemSets() {
		return userItemSets;
	}

	/**
	 * @param userItemSets
	 *            the userItemSets to set
	 */
	public void setUserItemSets(Map<Long, Set<OrderItemSet>> userItemSets) {
		this.userItemSets = userItemSets;
	}

}
