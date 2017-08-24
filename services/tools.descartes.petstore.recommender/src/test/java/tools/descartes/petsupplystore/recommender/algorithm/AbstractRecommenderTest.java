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
package tools.descartes.petsupplystore.recommender.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.recommender.IRecommender;

/**
 * Abstract Recommender Test.
 * 
 * @author Johannes Grohmann
 *
 */
public abstract class AbstractRecommenderTest {

	private List<OrderItem> trainOrderItems;

	private List<Order> trainOrders;

	private List<OrderItem> recommendSingle;

	private List<OrderItem> recommendMulti;

	private IRecommender algo;

	/**
	 * Run the dummy test.
	 * 
	 * trainOrders = [{10, user100}, {11, user101}, {12, user103}, {13, user104},
	 * 				  {14, user101}, {15, user101}, {16, user105}, {17, user106}]
	 * orderItems: 10 = {1, 2, 3}; 11 = {2, 4}; 12 = {1, 3, 4}; 13 = {2}; 14 = {2};
	 * 				15 = {2}; 16 = {2, 3, 4, 5}; 17 = {3};
	 */
	@Before
	public void setup() {
		setupAlgo();
		// train orders
		trainOrders = new ArrayList<>();

		Order or = new Order();
		or.setUserId(100);
		or.setId(10);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(101);
		or.setId(11);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(103);
		or.setId(12);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(104);
		or.setId(13);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(101);
		or.setId(14);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(101);
		or.setId(15);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(105);
		or.setId(16);
		trainOrders.add(or);

		or = new Order();
		or.setUserId(106);
		or.setId(17);
		trainOrders.add(or);

		// train items
		trainOrderItems = new ArrayList<OrderItem>();

		OrderItem o = new OrderItem();
		o.setProductId(1);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setOrderId(11);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(11);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(1);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(13);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(14);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(15);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(5);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setOrderId(17);
		trainOrderItems.add(o);

		// recommend Single
		recommendSingle = new ArrayList<>();
		o = new OrderItem();
		o.setProductId(2);
		recommendSingle.add(o);

		// recommend Multi
		recommendMulti = new ArrayList<>();
		o = new OrderItem();
		o.setProductId(3);
		recommendMulti.add(o);
		o = new OrderItem();
		o.setProductId(5);
		recommendMulti.add(o);
		o = new OrderItem();
		o.setProductId(6);
		recommendMulti.add(o);
	}

	/**
	 * Iniialize the algo.
	 */
	protected abstract void setupAlgo();

	/**
	 * Test for interface conformity.
	 */
	@Test
	public void testInterface() {
		try {
			getAlgo().recommendProducts(recommendMulti);
			Assert.fail("Recommender is supposed to throw an exception before being trained.");
		} catch (UnsupportedOperationException e) {
			// expected
		}

		getAlgo().train(trainOrderItems, trainOrders);

		// compare type
		List<Long> recommended = new ArrayList<Long>();
		recommended.add(new Long(-1));

		Assert.assertEquals(recommended.getClass(), getAlgo().recommendProducts(recommendMulti).getClass());
		testResults();

		Assert.assertEquals(new ArrayList<Long>(), getAlgo().recommendProducts(new ArrayList<>()));
		testResults();
	}

	/**
	 * Test the results.
	 */
	public abstract void testResults();

	/**
	 * @return the trainOrderItems
	 */
	public List<OrderItem> getTrainOrderItems() {
		return trainOrderItems;
	}

	/**
	 * @param trainOrderItems
	 *            the trainOrderItems to set
	 */
	public void setTrainOrderItems(List<OrderItem> trainOrderItems) {
		this.trainOrderItems = trainOrderItems;
	}

	/**
	 * @return the trainOrders
	 */
	public List<Order> getTrainOrders() {
		return trainOrders;
	}

	/**
	 * @param trainOrders
	 *            the trainOrders to set
	 */
	public void setTrainOrders(List<Order> trainOrders) {
		this.trainOrders = trainOrders;
	}

	/**
	 * @return the recommendSingle
	 */
	public List<OrderItem> getRecommendSingle() {
		return recommendSingle;
	}

	/**
	 * @param recommendSingle
	 *            the recommendSingle to set
	 */
	public void setRecommendSingle(List<OrderItem> recommendSingle) {
		this.recommendSingle = recommendSingle;
	}

	/**
	 * @return the recommendMulti
	 */
	public List<OrderItem> getRecommendMulti() {
		return recommendMulti;
	}

	/**
	 * @param recommendMulti
	 *            the recommendMulti to set
	 */
	public void setRecommendMulti(List<OrderItem> recommendMulti) {
		this.recommendMulti = recommendMulti;
	}

	/**
	 * @return the algo
	 */
	public IRecommender getAlgo() {
		return algo;
	}

	/**
	 * @param algo
	 *            the algo to set
	 */
	public void setAlgo(IRecommender algo) {
		this.algo = algo;
	}

}
