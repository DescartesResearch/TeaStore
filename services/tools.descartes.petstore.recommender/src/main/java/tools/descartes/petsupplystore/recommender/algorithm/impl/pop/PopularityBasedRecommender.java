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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import tools.descartes.petsupplystore.recommender.algorithm.AbstractRecommender;
import tools.descartes.petsupplystore.recommender.algorithm.OrderItemSet;

/**
 * A simple Recommender that makes recommendations based on general popularity.
 * 
 * @author Johannes Grohmann
 *
 */
public class PopularityBasedRecommender extends AbstractRecommender {

	//map with all count items for the corresponding purchase count
	private TreeMap<Long, List<Long>> popRanking;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.petsupplystore.recommender.algorithm.AbstractRecommender#execute(
	 * java.util.List)
	 */
	@Override
	protected List<Long> execute(List<Long> currentItems) {
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
		HashMap<Long, Long> tmp = new HashMap<>();
		// calculate product probabilities and saving them
		for (Set<OrderItemSet> set : getUserItemSets().values()) {
			for (OrderItemSet orderItemSet : set) {
				for (Long product : orderItemSet.getOrderset()) {
					if (tmp.containsKey(product)) {
						tmp.put(product, tmp.get(product).longValue() + 1);
					} else {
						tmp.put(product, 1L);
					}
				}
			}
		}
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
