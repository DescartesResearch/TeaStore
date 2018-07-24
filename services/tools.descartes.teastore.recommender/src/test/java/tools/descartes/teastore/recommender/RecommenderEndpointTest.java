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
package tools.descartes.teastore.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.recommender.servlet.TrainingSynchronizer;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.entities.OrderItem;

/**
 * Test for the RecommenderEndpoint.
 * 
 * @author Johannes Grohmann
 *
 */
public class RecommenderEndpointTest extends AbstractRecommenderRestTest {

	/**
	 * Prefix for all recommender rest calls.
	 */
	public static final String RECOMMENDER_REST_PREFIX = "http://localhost:" + RECOMMENDER_TEST_PORT + "/"
			+ Service.RECOMMENDER.getServiceName() + "/rest/";

	/**
	 * Endpoint for training.
	 */
	public static final String TRAIN_TARGET = RECOMMENDER_REST_PREFIX + "train";

	/**
	 * Endpoint for isReady.
	 */
	public static final String ISREADY_TARGET = TRAIN_TARGET + "/isready";

	/**
	 * Endpoint for time synchronization.
	 */
	public static final String TIMESTAMP_TARGET = TRAIN_TARGET + "/timestamp";

	/**
	 * Endpoint for recommending (multi).
	 */
	public static final String RECOMMEND_TARGET = RECOMMENDER_REST_PREFIX + "recommend";

	/**
	 * Endpoint for recommending (single).
	 */
	public static final String RECOMMEND_SINGLE_TARGET = RECOMMENDER_REST_PREFIX + "recommendsingle";

	/**
	 * Run the access test.
	 */
	@Test
	public void testRecommendInterface() {
		performInterfaceTests();
		performSynchronizationTests();
	}

	/**
	 * Tests the synchronization behavior with other recommenders.
	 */
	public void performSynchronizationTests() {
		// TEST SYNCHRONIZATION

		// train again
		Response response = ClientBuilder.newBuilder().build().target(RecommenderEndpointTest.TRAIN_TARGET)
				.request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		String str = response.readEntity(String.class);
		Assert.assertTrue("The return string must report the successful training.",
				str.startsWith("The (re)train was succesfully done."));
		// after the training, we get our timestamp
		response = ClientBuilder.newBuilder().build().target(RecommenderEndpointTest.TIMESTAMP_TARGET)
				.request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		str = response.readEntity(String.class);
		Assert.assertEquals(MockOtherRecommenderProvider.getTimestamp(), str);

		// TEST RESULTS
		// test recommendation succeeds, even when uid is not present

		// sample values
		ArrayList<OrderItem> list = new ArrayList<OrderItem>();
		list.add(new OrderItem());
		list.get(list.size() - 1).setProductId(92);
		list.add(new OrderItem());
		list.get(list.size() - 1).setProductId(761);
		list.add(new OrderItem());
		list.get(list.size() - 1).setProductId(354);
		list.add(new OrderItem());
		list.get(list.size() - 1).setProductId(23);
		list.add(new OrderItem());
		list.get(list.size() - 1).setProductId(41);
		String uid = "884";

		// without uid -> fallback (Popbased recommender)
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		List<Long> recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// these are the results of all orders
		List<Long> expected = Arrays.asList(191L, 298L, 88L, 4L, 7L, 425L, 22L, 180L, 160L, 706L);
		Assert.assertEquals(expected, recommended);

		// with uid -> Preprocessed Slope One (Env variable defined above)
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).queryParam("uid", uid)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());

		recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// these are the results of all orders
		expected = Arrays.asList(345L, 48L, 88L, 320L, 30L, 3L, 1L, 2L, 4L, 5L);
		Assert.assertEquals(expected, recommended);

		// CHANGE TIMESTAMP
		// we somewhat "cheat" do adapt the timestamp, in order to force an earlier
		// synchronization
		String newmax = "1495010140000";
		TrainingSynchronizer.getInstance().setMaxTime(Long.parseLong(newmax));

		// train again
		response = ClientBuilder.newBuilder().build().target(RecommenderEndpointTest.TRAIN_TARGET)
				.request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		str = response.readEntity(String.class);
		Assert.assertTrue("The return string must report the successful training.",
				str.startsWith("The (re)train was succesfully done."));
		// after the training, we get our timestamp
		response = ClientBuilder.newBuilder().build().target(RecommenderEndpointTest.TIMESTAMP_TARGET)
				.request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		str = response.readEntity(String.class);
		Assert.assertEquals(newmax, str);

		// without uid -> fallback (Popbased recommender)
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// these results are different with a different timestamp
		expected = Arrays.asList(191L, 298L, 88L, 4L, 7L, 22L, 180L, 160L, 706L, 264L);
		Assert.assertEquals(expected, recommended);

		// with uid -> Preprocessed Slope One (Env variable defined above)
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).queryParam("uid", uid)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());

		recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// these results actually did not change
		expected = Arrays.asList(345L, 48L, 88L, 320L, 30L, 3L, 1L, 2L, 4L, 5L);
		Assert.assertEquals(expected, recommended);

	}

	/**
	 * Performs the interface tests for the REST interface.
	 */
	public void performInterfaceTests() {
		// TEST RECOMMEND ENDPOINT
		// Assert PUT Method is not allowed
		Response response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET)
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert GET Method is not allowed
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert calling recommend with single entity fails
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(new OrderItem(), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_BAD_REQUEST, response.getStatus());

		List<OrderItem> list = new ArrayList<OrderItem>();

		// TEST RECOMMENDSINGLE ENDPOINT
		// Assert PUT Method is not allowed
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET)
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert GET Method is not allowed
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET)
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET)
				.request(MediaType.APPLICATION_JSON).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert calling recommend with list entity fails
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_BAD_REQUEST, response.getStatus());

		list = new ArrayList<OrderItem>();

		// TEST ISREADY ENDPOINT
		// Assert PUT Method is not allowed
		response = ClientBuilder.newBuilder().build().target(ISREADY_TARGET).request(MediaType.TEXT_PLAIN)
				.put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert POST Method is not allowed
		response = ClientBuilder.newBuilder().build().target(ISREADY_TARGET).request(MediaType.TEXT_PLAIN)
				.post(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build().target(ISREADY_TARGET).request(MediaType.TEXT_PLAIN).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());

		// Wait for training to finish
		try {
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			Assert.fail();
		}
		
		// Assert calling train via GET is OK
		response = ClientBuilder.newBuilder().build().target(ISREADY_TARGET).request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		// assert that string returns true
		String isready = response.readEntity(String.class);
		Assert.assertEquals(String.valueOf(true), isready);

		// TEST TRAIN ENDPOINT
		// Assert PUT Method is not allowed
		response = ClientBuilder.newBuilder().build().target(TRAIN_TARGET).request(MediaType.TEXT_PLAIN)
				.put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert POST Method is not allowed
		response = ClientBuilder.newBuilder().build().target(TRAIN_TARGET).request(MediaType.TEXT_PLAIN)
				.post(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build().target(TRAIN_TARGET).request(MediaType.TEXT_PLAIN).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());

		// Assert calling train via GET is OK
		response = ClientBuilder.newBuilder().build().target(TRAIN_TARGET).request(MediaType.TEXT_PLAIN).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());

		// test training process finishes
		String str = response.readEntity(String.class);
		Assert.assertTrue("The return string must report the successful training.",
				str.startsWith("The (re)train was succesfully done."));

		// TEST RECOMMENDATION
		// test recommendation process is now available
		list = new ArrayList<OrderItem>();
		String uid = "12345";
		// test recommendation succeeds, even when uid is not present
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_TARGET).queryParam("uid", uid)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		List<Long> recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// checking if a list was returned
		Assert.assertNotEquals("The given entity must not be null.", null, recommended);
		// checking if the list contains the right type
		Long[] arr = { 1L };
		try {
			recommended.toArray(arr);
		} catch (ArrayStoreException e) {
			Assert.fail("The list should contain Long ids.");
		}

		// TEST RECOMMENDATION SINGLE
		// checking if sending null fails
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET).queryParam("uid", uid)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_INTERNAL_SERVER_ERROR, response.getStatus());

		// test recommendation process is now available
		// check if sending without uid, does not fail
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(new OrderItem(), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		response = ClientBuilder.newBuilder().build().target(RECOMMEND_SINGLE_TARGET).queryParam("uid", uid)
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(new OrderItem(), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// checking if a list was returned
		Assert.assertNotEquals("The given entity must not be null.", null, recommended);
		// checking if the list contains the right type
		try {
			recommended.toArray(arr);
		} catch (ArrayStoreException e) {
			Assert.fail("The list should contain Long ids.");
		}

	}
}
