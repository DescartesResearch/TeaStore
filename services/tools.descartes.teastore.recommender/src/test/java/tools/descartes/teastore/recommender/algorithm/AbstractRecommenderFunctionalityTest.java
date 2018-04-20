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

import org.junit.Test;

/**
 * Abstract Recommender Test.
 * 
 * @author Johannes Grohmann
 *
 */
public abstract class AbstractRecommenderFunctionalityTest extends AbstractRecommenderTest {
	/**
	 * Tests the common functions which should usually be the same for all abstract
	 * recommender implementations.
	 */
	@Test
	public void testAbstractFunctions() {
		// train
		getAlgo().train(getTrainOrderItems(), getTrainOrders());

		Map<Long, Map<Long, Double>> newmatrix = new HashMap<>();

		// user100
		Map<Long, Double> entry = new HashMap<>();
		entry.put(1L, 1.0);
		entry.put(2L, 1.0);
		entry.put(3L, 1.0);
		newmatrix.put(100L, entry);
		// user101
		entry = new HashMap<>();
		entry.put(2L, 8.0);
		entry.put(4L, 2.0);
		newmatrix.put(101L, entry);
		// user103
		entry = new HashMap<>();
		entry.put(1L, 1.0);
		entry.put(3L, 1.0);
		entry.put(4L, 1.0);
		newmatrix.put(103L, entry);
		// user104
		entry = new HashMap<>();
		entry.put(2L, 2.0);
		newmatrix.put(104L, entry);
		// user105
		entry = new HashMap<>();
		entry.put(2L, 1.0);
		entry.put(3L, 1.0);
		entry.put(4L, 1.0);
		entry.put(5L, 1.0);
		newmatrix.put(105L, entry);
		// user106
		entry = new HashMap<>();
		entry.put(3L, 1.0);
		newmatrix.put(106L, entry);

		assertEquals(newmatrix, getAlgo().getUserBuyingMatrix());
	}
}
