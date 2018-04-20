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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tools.descartes.teastore.recommender.algorithm.AbstractRecommender;
import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;

/**
 * Recommender based on item-based collaborative filtering with the slope one
 * algorithm.
 * 
 * @author Johannes Grohmann
 *
 */
public class SlopeOneRecommender extends AbstractRecommender {

	/**
	 * Represents a matrix, assigning each itemid an average difference (in
	 * rating/buying) to any other itemid.
	 */
	private Map<Long, Map<Long, Double>> differences = new HashMap<>();

	/**
	 * Represents a matrix, counting the frequencies of each combination (i.e. users
	 * rating/buying both items).
	 */
	private Map<Long, Map<Long, Integer>> frequencies = new HashMap<>();

	/**
	 * @return the differences
	 */
	public Map<Long, Map<Long, Double>> getDifferences() {
		return differences;
	}

	/**
	 * @param differences
	 *            the differences to set
	 */
	public void setDifferences(Map<Long, Map<Long, Double>> differences) {
		this.differences = differences;
	}

	/**
	 * @return the frequencies
	 */
	public Map<Long, Map<Long, Integer>> getFrequencies() {
		return frequencies;
	}

	/**
	 * @param frequencies
	 *            the frequencies to set
	 */
	public void setFrequencies(Map<Long, Map<Long, Integer>> frequencies) {
		this.frequencies = frequencies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommender#
	 * execute(java.util.List)
	 */
	@Override
	protected List<Long> execute(Long userid, List<Long> currentItems) {
		if (userid == null) {
			throw new UseFallBackException(this.getClass().getName()
					+ " does not support null userids. Use a pseudouser or switch to another approach.");
		}
		if (getUserBuyingMatrix().get(userid) == null) {
			// this user has not bought anything yet, so we do not have any information
			throw new UseFallBackException("No user information.");
		}
		Map<Long, Double> importances = getUserVector(userid);
		return filterRecommendations(importances, currentItems);

	}

	/**
	 * Generates one row of the matrix for the given user. (Predicts the user score
	 * for each product ID.)
	 * 
	 * @param userid
	 *            The user to predict for
	 * @return A Map assigning each product ID a (predicted) score (for the given
	 *         user)
	 */
	protected Map<Long, Double> getUserVector(Long userid) {
		// This could be further optimized by moving this part into the pre-processing
		// step, but we want to have nicer performance behavior
		HashMap<Long, Double> importances = new HashMap<>();
		for (Long productid : getTotalProducts()) {
			try {
				importances.put(productid, calculateScoreForItem(userid, productid));
			} catch (NullPointerException e) {
				// this exception can be thrown if we have not enough information
				importances.put(productid, -1.0);
			}
		}
		return importances;
	}

	private double calculateScoreForItem(long userid, long itemid) {
		double score = 0;
		double cumWeights = 0;
		for (Entry<Long, Double> useritem : getUserBuyingMatrix().get(userid).entrySet()) {
			// if we find that the user actually bought this item before, we can return this
			// value
			// (considering it is his rating, we can directly return this rating)
			if (useritem.getKey() == itemid) {
				return useritem.getValue();
			}
			// if not, we can calculate the (expected) rating for that user based on item i
			int frequency = frequencies.get(useritem.getKey()).get(itemid);
			score += useritem.getValue() * frequency;
			score += differences.get(useritem.getKey()).get(itemid) * frequency;
			cumWeights += frequency;
		}
		// normalize
		return score / cumWeights;
	}

	@Override
	protected void executePreprocessing() {
		// The buying matrix is considered to be the rating
		// i.e. the more buys, the higher the rating
		buildDifferencesMatrices(getUserBuyingMatrix());
	}

	/**
	 * Based on the available data, calculate the relationships between the items
	 * and number of occurrences. Fill the difference and frequencies matrix.
	 * 
	 * @param data
	 *            The user rating matrix
	 */
	private void buildDifferencesMatrices(Map<Long, Map<Long, Double>> userRatingMatrix) {
		for (Map<Long, Double> uservalues : userRatingMatrix.values()) {
			for (Entry<Long, Double> singleRating : uservalues.entrySet()) {
				// if not present -> create
				if (!frequencies.containsKey(singleRating.getKey())) {
					frequencies.put(singleRating.getKey(), new HashMap<Long, Integer>());
					differences.put(singleRating.getKey(), new HashMap<Long, Double>());
				}
				// for all other ratings of that user
				for (Entry<Long, Double> otherRating : uservalues.entrySet()) {
					int currCount = 0;
					Integer count = frequencies.get(singleRating.getKey()).get(otherRating.getKey());
					if (count != null) {
						// count is != null, if the key is actually found
						// if so, we use the known count value as count, otherwise the count until now
						// is 0
						currCount = count.intValue();
					}

					double currDiff = 0;
					Double diff = differences.get(singleRating.getKey()).get(otherRating.getKey());
					if (diff != null) {
						// diff is != null, if the key is actually found
						// if so, we use the known difference value as currDiff, otherwise the diff
						// until now is 0.0
						currDiff = diff.doubleValue();
					}

					// get the diff value of this user
					double userdiff = singleRating.getValue() - otherRating.getValue();
					frequencies.get(singleRating.getKey()).put(otherRating.getKey(), currCount + 1);
					differences.get(singleRating.getKey()).put(otherRating.getKey(), currDiff + userdiff);
				}
			}
		}

		// now, transform the differences matrix into real differences (not just the sum
		// of all found differences)
		for (Long i : differences.keySet()) {
			for (Long j : differences.get(i).keySet()) {
				// for all matrix entries divide the differences by the sum of occurences
				double diffval = differences.get(i).get(j);
				double freq = frequencies.get(i).get(j);
				differences.get(i).put(j, diffval / freq);
			}
		}
	}
}
