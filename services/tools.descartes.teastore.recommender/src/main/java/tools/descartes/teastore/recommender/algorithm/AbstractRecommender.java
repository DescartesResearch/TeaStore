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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

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
	 * This represents the matrix assigning each user a frequency for each product
	 * ID. The frequency resembles the number of times, a user has bought that item.
	 */
	private Map<Long, Map<Long, Double>> userBuyingMatrix;

	/**
	 * This set maps a userId to a set, containing the corresponding OrderItemSets,
	 * i.e. all orders that were done by the user.
	 */
	private Map<Long, Set<OrderItemSet>> userItemSets;

	/**
	 * This is an enumeration of all available products seen during the training
	 * phase.
	 */
	private Set<Long> totalProducts;

	@Override
	public void train(List<OrderItem> orderItems, List<Order> orders) {
		long tic = System.currentTimeMillis();
		totalProducts = new HashSet<>();
		// first create order mapping unorderized
		Map<Long, OrderItemSet> unOrderizeditemSets = new HashMap<>();
		for (OrderItem orderItem : orderItems) {
			if (!unOrderizeditemSets.containsKey(orderItem.getOrderId())) {
				unOrderizeditemSets.put(orderItem.getOrderId(), new OrderItemSet());
				unOrderizeditemSets.get(orderItem.getOrderId()).setOrderId(orderItem.getOrderId());
			}
			unOrderizeditemSets.get(orderItem.getOrderId()).getOrderset().put(orderItem.getProductId(),
					orderItem.getQuantity());
			// see, if we already have our item
			if (!totalProducts.contains(orderItem.getProductId())) {
				// if not known yet -> add
				totalProducts.add(orderItem.getProductId());
			}
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
			itemSets.get(order).setUserId(order.getUserId());
			userItemSets.get(order.getUserId()).add(itemSets.get(order));
		}
		userBuyingMatrix = createUserBuyingMatrix(userItemSets);
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
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems)
			throws UnsupportedOperationException {
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
		return execute(userid, items);
	}

	/**
	 * Filters the given ranking of recommendations and deletes items that already
	 * are in the cart. Furthermore caps the recommendations and only uses the
	 * {@link AbstractRecommender#MAX_NUMBER_OF_RECOMMENDATIONS} highest rated
	 * recommendations.
	 * 
	 * @param priorityList
	 *            The unfiltered ranking assigning each recommended product ID a
	 *            score or an importance. Does not need to be sorted.
	 * @param currentItems
	 *            The list of item IDs that must NOT be contained in the returned
	 *            list.
	 * @return A sorted list of recommendations with a size not greater than
	 *         {@link AbstractRecommender#MAX_NUMBER_OF_RECOMMENDATIONS}
	 */
	protected List<Long> filterRecommendations(Map<Long, Double> priorityList, List<Long> currentItems) {
		TreeMap<Double, List<Long>> ranking = createRanking(priorityList);
		List<Long> reco = new ArrayList<>(MAX_NUMBER_OF_RECOMMENDATIONS);
		for (Double score : ranking.descendingKeySet()) {
			List<Long> productIds = ranking.get(score);
			for (long productId : productIds) {
				if (reco.size() < MAX_NUMBER_OF_RECOMMENDATIONS) {
					if (!currentItems.contains(productId)) {
						reco.add(productId);
					}
				} else {
					return reco;
				}
			}
		}
		return reco;
	}

	private TreeMap<Double, List<Long>> createRanking(Map<Long, Double> map) {
		// transforming the map into a treemap (for efficient access)
		TreeMap<Double, List<Long>> ranking = new TreeMap<Double, List<Long>>();
		for (Entry<Long, Double> entry : map.entrySet()) {
			List<Long> productIds = ranking.get(entry.getValue());
			if (productIds == null) {
				productIds = new ArrayList<>();
				ranking.put(entry.getValue(), productIds);
			}
			productIds.add(entry.getKey());
		}
		return ranking;
	}

	/**
	 * Has to be implemented by subclasses in order to perform actual
	 * recommendation.
	 * 
	 * @param userid
	 *            The id of the {@link User} to recommend for. May be null.
	 * @param currentItems
	 *            A list containing all ids of {@link OrderItem}s.
	 * @return List of all IDs of the {@link Product} entities that are recommended
	 *         to add to the cart. Does not contain any {@link Product} that is
	 *         already part of the given list of {@link OrderItem}s. Might be empty.
	 */
	protected abstract List<Long> execute(Long userid, List<Long> currentItems);

	private Order findOrder(List<Order> orders, long orderid) {
		for (Order order : orders) {
			if (order.getId() == orderid) {
				return order;
			}
		}
		return null;
	}

	/**
	 * @return the userBuyingMatrix
	 */
	public Map<Long, Map<Long, Double>> getUserBuyingMatrix() {
		return userBuyingMatrix;
	}

	/**
	 * @param userBuyingMatrix
	 *            the userBuyingMatrix to set
	 */
	public void setUserBuyingMatrix(Map<Long, Map<Long, Double>> userBuyingMatrix) {
		this.userBuyingMatrix = userBuyingMatrix;
	}

	/**
	 * @return the totalProducts
	 */
	public Set<Long> getTotalProducts() {
		return totalProducts;
	}

	/**
	 * @param totalProducts
	 *            the totalProducts to set
	 */
	public void setTotalProducts(Set<Long> totalProducts) {
		this.totalProducts = totalProducts;
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

	/**
	 * Transforms the list of orders into one matrix containing all user-IDs and
	 * their number of buys (i.e., their rating) of all product-IDs. A
	 * quantity/rating of a user is null, if the user did not buy that item. If the
	 * user bought one item at least once, the contained value (rating) is the
	 * number of times, he bought one given item.
	 * 
	 * @param useritemsets
	 *            A map assigning each user-ID all its OrderItemSets
	 * @return A Map representing a matrix of each user-ID assigning each item-ID
	 *         its number of buys (as double value)
	 */
	private static Map<Long, Map<Long, Double>> createUserBuyingMatrix(Map<Long, Set<OrderItemSet>> useritemsets) {
		Map<Long, Map<Long, Double>> matrix = new HashMap<>();
		// for each user
		for (Entry<Long, Set<OrderItemSet>> entry : useritemsets.entrySet()) {
			// create a new line for this user-ID
			Map<Long, Double> line = new HashMap<>();
			// for all orders of that user
			for (OrderItemSet orderset : entry.getValue()) {
				// for all orderitems of that orderset
				for (Entry<Long, Integer> product : orderset.getOrderset().entrySet()) {
					// if key was not known before -> first occurence
					if (!line.containsKey(product.getKey())) {
						line.put(product.getKey(), Double.valueOf(product.getValue()));
					} else {
						// if key was known before -> increase counter
						line.put(product.getKey(), Double.valueOf(line.get(product.getKey()) + product.getValue()));
					}
				}
			}
			// add this user-ID to the matrix
			matrix.put(entry.getKey(), line);
		}
		return matrix;
	}

}
