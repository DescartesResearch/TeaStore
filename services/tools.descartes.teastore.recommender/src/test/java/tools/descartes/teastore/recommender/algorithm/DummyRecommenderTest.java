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

import tools.descartes.teastore.recommender.algorithm.impl.DummyRecommender;

/**
 * Test for the Dummy Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
public class DummyRecommenderTest extends AbstractRecommenderFunctionalityTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tools.descartes.teastore.recommender.algorithm.AbstractRecommenderTest#
	 * setupAlgo()
	 */
	@Override
	protected void setupAlgo() {
		setAlgo(new DummyRecommender());

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
		// compare
		List<Long> recommended = new ArrayList<Long>();
		recommended.add(-1L);
		Assert.assertEquals(recommended,
				getAlgo().recommendProducts(getAllUsers().get(0).getId(), getRecommendSingle()));
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
		// compare
		List<Long> recommended = new ArrayList<Long>();
		recommended.add(-1L);

		Assert.assertEquals(recommended,
				getAlgo().recommendProducts(getAllUsers().get(0).getId(), getRecommendMulti()));
		Assert.assertEquals(recommended,
				getAlgo().recommendProducts(getAllUsers().get(1).getId(), getRecommendMulti()));
		Assert.assertEquals(recommended,
				getAlgo().recommendProducts(getAllUsers().get(4).getId(), getRecommendMulti()));
	}

}
