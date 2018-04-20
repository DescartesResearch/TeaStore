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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import tools.descartes.teastore.recommender.algorithm.impl.UseFallBackException;
import tools.descartes.teastore.recommender.algorithm.impl.orderbased.OrderBasedRecommender;
import tools.descartes.teastore.entities.OrderItem;

/**
 * Test for the Dummy Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
public class OrderBasedRecommenderTest extends AbstractRecommenderFunctionalityTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * setupAlgo()
	 */
	@Override
	protected void setupAlgo() {
		setAlgo(new OrderBasedRecommender());
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
		// test single
		evaluateForItemNo2(getAlgo().recommendProducts(100L, getRecommendSingle()));
		// test single with different order
		evaluateForItemNo2(getAlgo().recommendProducts(102L, getRecommendSingle()));
		// test single with null user
		evaluateForItemNo2(getAlgo().recommendProducts(null, getRecommendSingle()));

		// try other item with id 1
		List<OrderItem> recommend = new ArrayList<>();
		OrderItem o = new OrderItem();
		o.setProductId(1);
		recommend.add(o);
		List<Long> result = getAlgo().recommendProducts(100L, recommend);
		Assert.assertEquals(3L, result.get(0).longValue());
		try {
			Assert.assertEquals(2L, result.get(1).longValue());
			Assert.assertEquals(4L, result.get(2).longValue());
		} catch (AssertionError e) {
			// Result should contain 2 or 4 on position 2
			Assert.assertEquals(4L, result.get(1).longValue());
			Assert.assertEquals(2L, result.get(2).longValue());
		}
		Assert.assertEquals(3, result.size());

		recommend = new ArrayList<>();
		o = new OrderItem();
		o.setProductId(6);
		recommend.add(o);

		try {
			getAlgo().recommendProducts(100L, recommend);
			Assert.fail("Exception expected");
		} catch (UseFallBackException e) {
			// expected
		}
	}

	private void evaluateForItemNo2(List<Long> result) {
		try {
			Assert.assertEquals(3L, result.get(0).longValue());
			Assert.assertEquals(4L, result.get(1).longValue());
		} catch (AssertionError e) {
			// Result should contain 3 or 4 on position 1
			Assert.assertEquals(4L, result.get(0).longValue());
			Assert.assertEquals(3L, result.get(1).longValue());
		}
		try {
			Assert.assertEquals(1L, result.get(2).longValue());
			Assert.assertEquals(5L, result.get(3).longValue());
		} catch (AssertionError e) {
			// Result should contain 5 or 1 on position 3
			Assert.assertEquals(5L, result.get(2).longValue());
			Assert.assertEquals(1L, result.get(3).longValue());
		}
		Assert.assertEquals(4, result.size());
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
		// test multi
		evaluateForItemNo356(getAlgo().recommendProducts(100L, getRecommendMulti()));
		// test multi with different user
		evaluateForItemNo356(getAlgo().recommendProducts(102L, getRecommendMulti()));

		// test multi with null user
		evaluateForItemNo356(getAlgo().recommendProducts(null, getRecommendMulti()));

		// test multi with different user
		evaluateForItemNo356(getAlgo().recommendProducts(104L, getRecommendMulti()));

		// test multi with different user
		evaluateForItemNo356(getAlgo().recommendProducts(105L, getRecommendMulti()));

		// test multi with different user
		evaluateForItemNo356(getAlgo().recommendProducts(101L, getRecommendMulti()));

	}

	private void evaluateForItemNo356(List<Long> result) {
		try {
			Assert.assertEquals(2L, result.get(0).longValue());
			Assert.assertEquals(4L, result.get(1).longValue());
		} catch (AssertionError e) {
			// Result should contain 2 or 4 on position 1
			Assert.assertEquals(4L, result.get(0).longValue());
			Assert.assertEquals(2L, result.get(1).longValue());
		}
		Assert.assertEquals(1L, result.get(2).longValue());
		Assert.assertEquals(3, result.size());
	}

}
