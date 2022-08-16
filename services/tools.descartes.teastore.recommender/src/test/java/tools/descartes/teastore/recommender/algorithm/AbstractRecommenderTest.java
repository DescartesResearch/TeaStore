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
import org.junit.Before;
import org.junit.Test;

import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.User;

/**
 * Abstract Recommender Test.
 * 
 * @author Johannes Grohmann
 *
 */
public abstract class AbstractRecommenderTest {

	private List<OrderItem> trainOrderItems;

	private List<Order> trainOrders;

	private List<User> allUsers;

	private List<OrderItem> recommendSingle;

	private List<OrderItem> recommendMulti;

	private AbstractRecommender algo;

	/**
	 * Run the setup.
	 * 
	 * trainOrders = [{10, user100}, {11, user101}, {12, user103}, {13, user104},
	 * {14, user101}, {15, user101}, {16, user105}, {17, user106}] <br>
	 * orderItems: 10 = {1, 2, 3}; 11 = {2^5,4^2}; 12 = {1, 3, 4}; 13 = {2^2}; 14 =
	 * {2^2}; 15 = {2}; 16 = {2, 3, 4, 5}; 17 = {3};
	 */
	@Before
	public void setup() {
		setupAlgo();
		// create users
		allUsers = new ArrayList<>();

		User u = new User();
		u.setId(100);
		u.setEmail("u100@testemail.com");
		u.setRealName("User 100");
		u.setUserName("u100");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(101);
		u.setEmail("u101@testemail.com");
		u.setRealName("User 101");
		u.setUserName("u101");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(102);
		u.setEmail("u102@testemail.com");
		u.setRealName("User 102");
		u.setUserName("u102");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(103);
		u.setEmail("u100@testemail.com");
		u.setRealName("User 103");
		u.setUserName("u103");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(104);
		u.setEmail("u104@testemail.com");
		u.setRealName("User 104");
		u.setUserName("u104");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(105);
		u.setEmail("u105@testemail.com");
		u.setRealName("User 105");
		u.setUserName("u105");
		u.setPassword("Bad password");
		allUsers.add(u);

		u.setId(106);
		u.setEmail("u106@testemail.com");
		u.setRealName("User 106");
		u.setUserName("u106");
		u.setPassword("Bad password");
		allUsers.add(u);

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
		o.setQuantity(1);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(1);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setQuantity(1);
		o.setOrderId(10);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setQuantity(2);
		o.setOrderId(11);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(5);
		o.setOrderId(11);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setQuantity(1);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setQuantity(1);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(1);
		o.setQuantity(1);
		o.setOrderId(12);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(2);
		o.setOrderId(13);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(2);
		o.setOrderId(14);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(1);
		o.setOrderId(15);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(2);
		o.setQuantity(1);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setQuantity(1);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(4);
		o.setQuantity(1);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(5);
		o.setQuantity(1);
		o.setOrderId(16);
		trainOrderItems.add(o);

		o = new OrderItem();
		o.setProductId(3);
		o.setQuantity(1);
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
			getAlgo().recommendProducts(allUsers.get(2).getId(), recommendMulti);
			Assert.fail("Recommender is supposed to throw an exception before being trained.");
		} catch (UnsupportedOperationException e) {
			// expected
		}

		getAlgo().train(trainOrderItems, trainOrders);

		// compare type
		List<Long> recommended = new ArrayList<Long>();
		recommended.add(-1L);

		Assert.assertEquals(recommended.getClass(),
				getAlgo().recommendProducts(allUsers.get(2).getId(), recommendMulti).getClass());

		Assert.assertEquals(new ArrayList<Long>(),
				getAlgo().recommendProducts(allUsers.get(1).getId(), new ArrayList<>()));
	}

	/**
	 * Test the results.
	 */
	@Test
	public void testResults() {
		getAlgo().train(getTrainOrderItems(), getTrainOrders());
		testSingleResults();
		testMultiResults();
	}

	/**
	 * Test the results of the single recommender interface.
	 */
	public abstract void testSingleResults();

	/**
	 * Test the results of the multi recommender interface.
	 */
	public abstract void testMultiResults();

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
	public AbstractRecommender getAlgo() {
		return algo;
	}

	/**
	 * @param algo
	 *            the algo to set
	 */
	public void setAlgo(AbstractRecommender algo) {
		this.algo = algo;
	}

	/**
	 * @return the allUsers
	 */
	public List<User> getAllUsers() {
		return allUsers;
	}

	/**
	 * @param allUsers
	 *            the allUsers to set
	 */
	public void setAllUsers(List<User> allUsers) {
		this.allUsers = allUsers;
	}

}
