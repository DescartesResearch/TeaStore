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
package tools.descartes.teastore.recommender.algorithm.impl.orderbased;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tools.descartes.teastore.recommender.algorithm.AbstractRecommender;
import tools.descartes.teastore.recommender.algorithm.OrderItemSet;
import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;

/**
 * A simple Recommender that makes recommendations based on an order-based
 * nearest-neighbor heuristic.
 * 
 * @author Johannes Grohmann
 *
 */
public class OrderBasedRecommender extends AbstractRecommender {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommender#
	 * execute( java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		// Map with all product IDs and how often they have been bought in the same cart
		// with one of the items in the current cart
		HashMap<Long, Double> counts = new HashMap<>();
		// treat all products in the current cart equally, and sum all the occurrences
		for (Long product : currentItems) {
			addAllCountsOfProduct(counts, product);
		}

		if (counts.isEmpty()) {
			throw new UseFallBackException(
					"No item was bought together with the current cart. Therefore, all counts are 0.");
		}

		// the count list contains all items in the current cart
		// however, this is fine, as it is filtered
		return filterRecommendations(counts, currentItems);
	}

	/**
	 * Adds the counts of the given product to the given count list.
	 * 
	 * @param counts
	 *            The count list, assinging each product id, how often it was bought
	 *            with the given product.
	 * @param product
	 *            The product id of the specific product.
	 */
	private void addAllCountsOfProduct(HashMap<Long, Double> counts, Long product) {
		for (Set<OrderItemSet> set : getUserItemSets().values()) {
			// ignore which user bought which set
			for (OrderItemSet orderset : set) {
				// look through all orders bought
				if (orderset.getOrderset().keySet().contains(product)) {
					for (Long o : orderset.getOrderset().keySet()) {
						// we count all order of the set (including the product we are currently looking
						// at)
						if (counts.containsKey(o)) {
							// we do not count the NUMBER of items, just if the item occurred in the order
							counts.put(o, counts.get(o) + 1);
						} else {
							counts.put(o, 1.0);
						}
					}
				}
			}
		}
	}
}
