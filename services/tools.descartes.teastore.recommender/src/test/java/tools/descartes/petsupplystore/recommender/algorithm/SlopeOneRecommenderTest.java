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
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.teastore.recommender.algorithm.impl.cf.SlopeOneRecommender;

/**
 * Test for the Dummy Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
public class SlopeOneRecommenderTest extends AbstractRecommenderFunctionalityTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * setupAlgo()
	 */
	@Override
	protected void setupAlgo() {
		setAlgo(new SlopeOneRecommender());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * testResults()
	 */
	@Override
	public void testSingleResults() {
		// check resulting matrices
		checkDiffMatrix();
		checkFreqMatrix();
		// test single
		List<Long> result = getAlgo().recommendProducts(100L, getRecommendSingle());
		Assert.assertEquals(4L, result.get(0).longValue());
		try {
			Assert.assertEquals(3L, result.get(1).longValue());
			Assert.assertEquals(1L, result.get(2).longValue());
		} catch (AssertionError e) {
			// Result should contain 3 or 1 on position 1
			Assert.assertEquals(1L, result.get(1).longValue());
			Assert.assertEquals(3L, result.get(2).longValue());
		}
		Assert.assertEquals(5L, result.get(3).longValue());
		Assert.assertEquals(4, result.size());

		// test single with different user
		try {
			result = getAlgo().recommendProducts(102L, getRecommendSingle());
			Assert.fail("Exception expected");
		} catch (UseFallBackException e) {
			// expected
		}
		// test single with null user
		try {
			result = getAlgo().recommendProducts(null, getRecommendSingle());
			Assert.fail("Exception expected");
		} catch (UseFallBackException e) {
			// expected
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * testResults()
	 */
	@Override
	public void testMultiResults() {
		// check that matrices did not change
		checkDiffMatrix();
		checkFreqMatrix();
		// test multi
		List<Long> result = getAlgo().recommendProducts(100L, getRecommendMulti());
		Assert.assertEquals(4L, result.get(0).longValue());
		try {
			Assert.assertEquals(2L, result.get(1).longValue());
			Assert.assertEquals(1L, result.get(2).longValue());
		} catch (AssertionError e) {
			// Result should contain 2 or 1 on position 1
			Assert.assertEquals(1L, result.get(1).longValue());
			Assert.assertEquals(2L, result.get(2).longValue());
		}
		Assert.assertEquals(3, result.size());

		// test multi with different user
		try {
			result = getAlgo().recommendProducts(102L, getRecommendMulti());
			Assert.fail("Exception expected");
		} catch (UseFallBackException e) {
			// expected
		}
		
		// test multi with null user
		try {
			result = getAlgo().recommendProducts(null, getRecommendMulti());
			Assert.fail("Exception expected");
		} catch (UseFallBackException e) {
			// expected
		}

		// test multi with different user
		result = getAlgo().recommendProducts(104L, getRecommendMulti());
		Assert.assertEquals(4L, result.get(0).longValue());
		try {
			Assert.assertEquals(2L, result.get(1).longValue());
			Assert.assertEquals(1L, result.get(2).longValue());
		} catch (AssertionError e) {
			// Result should contain 2 or 1 on position 1
			Assert.assertEquals(1L, result.get(1).longValue());
			Assert.assertEquals(2L, result.get(2).longValue());
		}
		Assert.assertEquals(3, result.size());

		// test multi with different user
		result = getAlgo().recommendProducts(105L, getRecommendMulti());
		try {
			Assert.assertEquals(4L, result.get(0).longValue());
			Assert.assertEquals(2L, result.get(1).longValue());
		}  catch (AssertionError e) {
			// Result should contain 2 or 1 on position 1
			Assert.assertEquals(2L, result.get(0).longValue());
			Assert.assertEquals(4L, result.get(1).longValue());
		}
		Assert.assertEquals(1L, result.get(2).longValue());
		Assert.assertEquals(3, result.size());

		// test multi with different user
		result = getAlgo().recommendProducts(101L, getRecommendMulti());
		Assert.assertEquals(2L, result.get(0).longValue());
		Assert.assertEquals(1L, result.get(1).longValue());
		Assert.assertEquals(4L, result.get(2).longValue());
		Assert.assertEquals(3, result.size());

		// check that matrices still not changed
		checkDiffMatrix();
		checkFreqMatrix();
	}

	private void checkDiffMatrix() {
		Map<Long, Map<Long, Double>> differences = new HashMap<>();

		// item 1
		Map<Long, Double> entry = new HashMap<>();
		entry.put(1L, 0.0);
		entry.put(2L, 0.0);
		entry.put(3L, 0.0);
		entry.put(4L, 0.0);
		differences.put(1L, entry);
		// item 2
		entry = new HashMap<>();
		entry.put(1L, 0.0);
		entry.put(2L, 0.0);
		entry.put(3L, 0.0);
		entry.put(4L, 3.0);
		entry.put(5L, 0.0);
		differences.put(2L, entry);
		// item 3
		entry = new HashMap<>();
		entry.put(1L, 0.0);
		entry.put(2L, 0.0);
		entry.put(3L, 0.0);
		entry.put(4L, 0.0);
		entry.put(5L, 0.0);
		differences.put(3L, entry);
		// item 4
		entry = new HashMap<>();
		entry.put(1L, 0.0);
		entry.put(2L, -3.0);
		entry.put(3L, 0.0);
		entry.put(4L, 0.0);
		entry.put(5L, 0.0);
		differences.put(4L, entry);
		// item 5
		entry = new HashMap<>();
		entry.put(2L, 0.0);
		entry.put(3L, 0.0);
		entry.put(4L, 0.0);
		entry.put(5L, 0.0);
		differences.put(5L, entry);

		assertEquals(differences, ((SlopeOneRecommender) getAlgo()).getDifferences());
	}

	private void checkFreqMatrix() {
		Map<Long, Map<Long, Integer>> frequencies = new HashMap<>();

		// item 1
		Map<Long, Integer> entry = new HashMap<>();
		entry.put(1L, 2);
		entry.put(2L, 1);
		entry.put(3L, 2);
		entry.put(4L, 1);
		frequencies.put(1L, entry);
		// item 2
		entry = new HashMap<>();
		entry.put(1L, 1);
		entry.put(2L, 4);
		entry.put(3L, 2);
		entry.put(4L, 2);
		entry.put(5L, 1);
		frequencies.put(2L, entry);
		// item 3
		entry = new HashMap<>();
		entry.put(1L, 2);
		entry.put(2L, 2);
		entry.put(3L, 4);
		entry.put(4L, 2);
		entry.put(5L, 1);
		frequencies.put(3L, entry);
		// item 4
		entry = new HashMap<>();
		entry.put(1L, 1);
		entry.put(2L, 2);
		entry.put(3L, 2);
		entry.put(4L, 3);
		entry.put(5L, 1);
		frequencies.put(4L, entry);
		// item 5
		entry = new HashMap<>();
		entry.put(2L, 1);
		entry.put(3L, 1);
		entry.put(4L, 1);
		entry.put(5L, 1);
		frequencies.put(5L, entry);

		assertEquals(frequencies, ((SlopeOneRecommender) getAlgo()).getFrequencies());
	}

}
