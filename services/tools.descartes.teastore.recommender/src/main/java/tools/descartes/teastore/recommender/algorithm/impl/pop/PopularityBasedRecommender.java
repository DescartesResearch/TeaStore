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
package tools.descartes.teastore.recommender.algorithm.impl.pop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tools.descartes.teastore.recommender.algorithm.AbstractRecommender;

/**
 * A simple Recommender that makes recommendations based on general popularity.
 * 
 * @author Johannes Grohmann
 *
 */
public class PopularityBasedRecommender extends AbstractRecommender {

	/**
	 * Map with all product IDs and their corresponding total purchase counts.
	 */
	private HashMap<Long, Double> counts;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommender#
	 * execute( java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		return filterRecommendations(counts, currentItems);
	}

	@Override
	protected void executePreprocessing() {
		// assigns each product a quantity
		counts = new HashMap<>();
		// calculate product frequencies
		for (Map<Long, Double> usermap : getUserBuyingMatrix().values()) {
			for (Entry<Long, Double> product : usermap.entrySet()) {
				if (!counts.containsKey(product.getKey())) {
					counts.put(product.getKey(), product.getValue());
				} else {
					counts.put(product.getKey(), counts.get(product.getKey()) + product.getValue());
				}
			}
		}

	}
}
