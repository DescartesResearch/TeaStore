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
package tools.descartes.petstore.recommender.algorithm.impl.pop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import tools.descartes.petstore.recommender.algorithm.AbstractRecommender;
import tools.descartes.petstore.recommender.algorithm.OrderItemSet;

/**
 * A simple Recommender that makes recommendations based on general popularity.
 * 
 * @author Johannes Grohmann
 *
 */
public class PopularityBasedRecommender extends AbstractRecommender {

	private TreeSet<CountItem> popRanking;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.petstore.recommender.algorithm.AbstractRecommender#execute(
	 * java.util.List)
	 */
	@Override
	protected List<Long> execute(List<Long> currentItems) {
		List<Long> reco = new ArrayList<>(MAX_NUMBER_OF_RECOMMENDATIONS);
		for (Iterator<CountItem> iterator = popRanking.descendingSet().iterator(); iterator.hasNext()
				&& reco.size() < MAX_NUMBER_OF_RECOMMENDATIONS;) {
			CountItem product = (CountItem) iterator.next();
			if (!currentItems.contains(product.getProductId())) {
				reco.add(product.getProductId());
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
		popRanking = new TreeSet<>();
		for (Entry<Long, Long> entry : tmp.entrySet()) {
			popRanking.add(new CountItem(entry.getKey(), entry.getValue()));
		}
	}

}
