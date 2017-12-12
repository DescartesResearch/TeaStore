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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.petsupplystore.recommender.algorithm.impl.pop.PopularityBasedRecommender;

/**
 * A strategy selector for the Recommender functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public final class RecommenderSelector implements IRecommender {

	private static final Logger LOG = LoggerFactory.getLogger(RecommenderSelector.class);

	private static RecommenderSelector instance;

	private IRecommender fallbackrecommender;

	private IRecommender recommender;

	/**
	 * Private Constructor.
	 */
	private RecommenderSelector() {
		recommender = new PopularityBasedRecommender();
		fallbackrecommender = new PopularityBasedRecommender();
	}

	@Override
	public List<Long> recommendProducts(Long userid, List<OrderItem> currentItems)
			throws UnsupportedOperationException {
		try {
			return recommender.recommendProducts(userid, currentItems);
		} catch (UseFallBackException e) {
			LOG.warn(
					"Executing" + recommender.getClass().getName() + "recommender failed. Using fallback recommender.");
			return fallbackrecommender.recommendProducts(userid, currentItems);
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.petsupplystore.recommender.IRecommender#train(java.util.List,
	 * java.util.List)
	 */
	@Override
	public void train(List<OrderItem> orderItems, List<Order> orders) {
		recommender.train(orderItems, orders);
		fallbackrecommender.train(orderItems, orders);
	}

}
