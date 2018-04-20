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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import tools.descartes.teastore.recommender.algorithm.impl.cf.PreprocessedSlopeOneRecommender;

/**
 * Test for the Dummy Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
public class PreprocessedSlopeOneRecommenderTest extends SlopeOneRecommenderTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * setupAlgo()
	 */
	@Override
	protected void setupAlgo() {
		setAlgo(new PreprocessedSlopeOneRecommender());
	}

	@Override
	public void testSingleResults() {
		super.testSingleResults();
		checkUserPredictionMatrix();
	}
	
	@Override
	public void testMultiResults() {
		super.testMultiResults();
		checkUserPredictionMatrix();
	}

	private void checkUserPredictionMatrix() {
		Map<Long, Map<Long, Double>> ratings = new HashMap<>();

		// user 100 {1=1.0, 2=1.0, 3=1.0, 4=2.2, 5=-1.0}
		Map<Long, Double> entry = new HashMap<>();
		entry.put(1L, 1.0);
		entry.put(2L, 1.0);
		entry.put(3L, 1.0);
		entry.put(4L, 2.2);
		entry.put(5L, -1.0);
		ratings.put(100L, entry);
		// user 101 {1=5.0, 2=8.0, 3=5.0, 4=2.0, 5=5.0}
		entry = new HashMap<>();
		entry.put(1L, 5.0);
		entry.put(2L, 8.0);
		entry.put(3L, 5.0);
		entry.put(4L, 2.0);
		entry.put(5L, 5.0);
		ratings.put(101L, entry);
		// user 103 {1=1.0, 2=-0.2, 3=1.0, 4=1.0, 5=-1.0}
		entry = new HashMap<>();
		entry.put(1L, 1.0);
		entry.put(2L, -0.2);
		entry.put(3L, 1.0);
		entry.put(4L, 1.0);
		entry.put(5L, -1.0);
		ratings.put(103L, entry);
		// user 104 {1=2.0, 2=2.0, 3=2.0, 4=5.0, 5=2.0}
		entry = new HashMap<>();
		entry.put(1L, 2.0);
		entry.put(2L, 2.0);
		entry.put(3L, 2.0);
		entry.put(4L, 5.0);
		entry.put(5L, 2.0);
		ratings.put(104L, entry);
		// user 105 {1=-1.0, 2=1.0, 3=1.0, 4=1.0, 5=1.0}
		entry = new HashMap<>();
		entry.put(1L, -1.0);
		entry.put(2L, 1.0);
		entry.put(3L, 1.0);
		entry.put(4L, 1.0);
		entry.put(5L, 1.0);
		ratings.put(105L, entry);
		// user 106 {1=1.0, 2=1.0, 3=1.0, 4=1.0, 5=1.0}
		entry = new HashMap<>();
		entry.put(1L, 1.0);
		entry.put(2L, 1.0);
		entry.put(3L, 1.0);
		entry.put(4L, 1.0);
		entry.put(5L, 1.0);
		ratings.put(106L, entry);

		assertEquals(ratings, ((PreprocessedSlopeOneRecommender) getAlgo()).getPredictedRatings());

	}

}
