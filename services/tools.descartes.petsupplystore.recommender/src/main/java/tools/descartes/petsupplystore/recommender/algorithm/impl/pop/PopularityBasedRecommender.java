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
package tools.descartes.petsupplystore.recommender.algorithm.impl.pop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import tools.descartes.petsupplystore.recommender.algorithm.AbstractRecommender;

/**
 * A simple Recommender that makes recommendations based on general popularity.
 * 
 * @author Johannes Grohmann
 *
 */
public class PopularityBasedRecommender extends AbstractRecommender {

	// map with all count items for the corresponding purchase count
	private TreeMap<Long, List<Long>> popRanking;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.petsupplystore.recommender.algorithm.AbstractRecommender#
	 * execute( java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		List<Long> reco = new ArrayList<>(MAX_NUMBER_OF_RECOMMENDATIONS);
		for (Long count : popRanking.descendingKeySet()) {
			List<Long> productIds = popRanking.get(count);
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

	@Override
	protected void executePreprocessing() {
		// assigns each product a quantity
		HashMap<Long, Long> tmp = new HashMap<>();
		// calculate product frequencies
		for (Map<Long, Double> usermap : getUserBuyingMatrix().values()) {
			for (Entry<Long, Double> product : usermap.entrySet()) {
				if (!tmp.containsKey(product.getKey())) {
					tmp.put(product.getKey(), product.getValue().longValue());
				} else {
					tmp.put(product.getKey(), tmp.get(product.getKey()) + product.getValue().longValue());
				}
			}
		}
		// and saving them in a treemap (for efficient access)
		popRanking = new TreeMap<Long, List<Long>>();
		for (Entry<Long, Long> entry : tmp.entrySet()) {
			List<Long> productIds = popRanking.get(entry.getValue());
			if (productIds == null) {
				productIds = new ArrayList<>();
				popRanking.put(entry.getValue(), productIds);
			}
			productIds.add(entry.getKey());
		}
	}

}
