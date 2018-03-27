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
package tools.descartes.teastore.recommender.algorithm.impl.cf;

import java.util.HashMap;
import java.util.Map;

/**
 * Recommender based on item-based collaborative filtering with the slope one
 * algorithm.
 * 
 * @author Johannes Grohmann
 *
 */
public class PreprocessedSlopeOneRecommender extends SlopeOneRecommender {

	/**
	 * Represents a matrix, assigning each user a calculated score for each item.
	 * This score can be used to recommend items.
	 */
	private Map<Long, Map<Long, Double>> predictedRatings;

	/**
	 * @return the predictedRatings
	 */
	public Map<Long, Map<Long, Double>> getPredictedRatings() {
		return predictedRatings;
	}

	/**
	 * @param predictedRatings
	 *            the predictedRatings to set
	 */
	public void setPredictedRatings(Map<Long, Map<Long, Double>> predictedRatings) {
		this.predictedRatings = predictedRatings;
	}

	@Override
	protected Map<Long, Double> getUserVector(Long userid) {
		// improve performance by preprocessing and storing userids
		return predictedRatings.get(userid);
	}

	@Override
	protected void executePreprocessing() {
		super.executePreprocessing();
		predictedRatings = new HashMap<>();
		// Moving the matrix calculation to the preprocessing to optimize runtime
		// behavior
		for (Long userid : getUserBuyingMatrix().keySet()) {
			// for all known users
			Map<Long, Double> pred = super.getUserVector(userid);
			predictedRatings.put(userid, pred);
		}
	}
}
