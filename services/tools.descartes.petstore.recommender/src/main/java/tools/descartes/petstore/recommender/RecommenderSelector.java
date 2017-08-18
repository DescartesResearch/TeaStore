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
package tools.descartes.petstore.recommender;

import java.util.List;

import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.recommender.algorithm.impl.pop.PopularityBasedRecommender;

/**
 * A strategy selector for the Recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public final class RecommenderSelector implements IRecommender {

	private static RecommenderSelector instance;

	private IRecommender recommender;

	/**
	 * Private Constructor.
	 */
	private RecommenderSelector() {
		recommender = new PopularityBasedRecommender();
	}

	@Override
	public List<Long> recommendProducts(List<OrderItem> currentItems) throws UnsupportedOperationException {
		return recommender.recommendProducts(currentItems);
	}

	/**
	 * Returns the instance of this Singleton or creates a new one, if this is the
	 * first call of this method.
	 * 
	 * @return The instance of this class.
	 */
	public static RecommenderSelector getInstance() {
		if (instance == null) {
			instance = new RecommenderSelector();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see tools.descartes.petstore.recommender.IRecommender#train(java.util.List, java.util.List)
	 */
	@Override
	public void train(List<OrderItem> orderItems, List<Order> orders) {
		recommender.train(orderItems, orders);
	}

}
