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
package tools.descartes.petstore.recommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.rest.LoadBalancedCRUDOperations;

/**
 * Test for the RecommenderEndpoint.
 * 
 * @author Johannes Grohmann
 *
 */
public class RecommenderPerformanceTest extends AbstractRecommenderRestTest {

	/**
	 * The number of times, the experiment should be repeated (training and
	 * recommendation).
	 */
	public static final int NUMBER_OF_REPEATS = 10;

	/**
	 * The number of recommendations to be done for one trained instance.
	 */
	public static final int NUMBER_OF_RECOMMENDATIONS_PER_REPEAT = 10000;

	/**
	 * The number of single recommendations to be done for one trained instance.
	 */
	public static final int NUMBER_OF_SINGLE_RECOMMENDATIONS_PER_REPEAT = 2000;

	/**
	 * The maximum numbers of items in one cart. The actual number is randomized.
	 */
	public static final int MAX_NUMBER_OF_ITEMS = 10;

	private List<Product> products;

	private Random r = new Random();

	private ArrayList<List<OrderItem>> orders;

	/**
	 * Retrieves products from the database via REST and creates testcase instances
	 * to send to the server.
	 */
	public void retrieveDatabaseItems() {
		products = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "products", Product.class, -1, -1);
		orders = new ArrayList<>();
		// create as many testcases as necessary
		for (int j = 0; j < Math.max(NUMBER_OF_RECOMMENDATIONS_PER_REPEAT,
				NUMBER_OF_SINGLE_RECOMMENDATIONS_PER_REPEAT); j++) {
			// create Requests
			List<OrderItem> list = new ArrayList<OrderItem>();
			int items = r.nextInt(MAX_NUMBER_OF_ITEMS - 1);
			for (int i = 0; i < items + 1; i++) {
				OrderItem o = new OrderItem();
				o.setProductId(products.get(r.nextInt(products.size())).getId());
				list.add(o);
			}
			orders.add(list);
		}
	}

	/**
	 * Run the access test.
	 * @Test
	 */
	public void testRecommendEndpoint() {
		retrieveDatabaseItems();
		long traintime = 0;
		long recotime = 0;
		long singlerecotime = 0;
		for (int i = 0; i < NUMBER_OF_REPEATS; i++) {
			long tic = System.currentTimeMillis();
			triggerRetrain();
			traintime += System.currentTimeMillis() - tic;
			for (int j = 0; j < NUMBER_OF_RECOMMENDATIONS_PER_REPEAT; j++) {
				tic = System.currentTimeMillis();
				recommend(j);
				recotime += System.currentTimeMillis() - tic;
			}
			for (int j = 0; j < NUMBER_OF_SINGLE_RECOMMENDATIONS_PER_REPEAT; j++) {
				tic = System.currentTimeMillis();
				recommendSingle(j);
				singlerecotime += System.currentTimeMillis() - tic;
			}
		}
	}

	/**
	 * 
	 */
	private void recommendSingle(int index) {
		Response response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(orders.get(index).iterator().next(), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
	}

	/**
	 * 
	 */
	private void recommend(int index) {
		Response response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(orders.get(index), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
	}

	private void triggerRetrain() {
		Response response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
	}
}
