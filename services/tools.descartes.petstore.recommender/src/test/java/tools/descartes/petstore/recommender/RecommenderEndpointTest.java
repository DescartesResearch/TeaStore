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

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.petstore.entities.OrderItem;

/**
 * Test for the RecommenderEndpoint.
 * 
 * @author Johannes Grohmann
 *
 */
public class RecommenderEndpointTest extends AbstractRecommenderRestTest {
	/**
	 * Run the access test.
	 */
	@Test
	public void testRecommendEndpoint() {
		// TEST RECOMMEND ENDPOINT
		// Assert PUT Method is not allowed
		Response response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert GET Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert calling recommend with single entity fails
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(new OrderItem(), MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_BAD_REQUEST, response.getStatus());

		// Assert calling recommend via POST method fails before calling train
		List<OrderItem> list = new ArrayList<OrderItem>();
		//TODO Currently fails
//		response = ClientBuilder.newBuilder().build()
//				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
//				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
//		Assert.assertEquals(org.apache.catalina.connector.Response.SC_INTERNAL_SERVER_ERROR, response.getStatus());

		// TEST RECOMMENDSINGLE ENDPOINT
		// Assert PUT Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert GET Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert calling recommend with list entity fails
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_BAD_REQUEST, response.getStatus());

		// Assert calling recommend via POST method fails before calling train
		list = new ArrayList<OrderItem>();
		//TODO Currently fails
//		response = ClientBuilder.newBuilder().build()
//				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
//				.request(MediaType.APPLICATION_JSON).post(Entity.entity(new OrderItem(), MediaType.APPLICATION_JSON));
//		Assert.assertEquals(org.apache.catalina.connector.Response.SC_INTERNAL_SERVER_ERROR, response.getStatus());

		// TEST TRAIN ENDPOINT
		// Assert PUT Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert POST Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).post(Entity.text(""));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());
		// Assert DELETE Method is not allowed
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).delete();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_METHOD_NOT_ALLOWED, response.getStatus());

		// Assert calling train via GET is OK
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());

		// test training process finishes
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/train")
				.request(MediaType.APPLICATION_JSON).get();
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		String str = response.readEntity(String.class);
		Assert.assertTrue("The return string must report the successful training.",
				str.startsWith("The (re)train was succesfully done."));

		// TEST RECOMMENDATION
		// test recommendation process is now available
		list = new ArrayList<OrderItem>();
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommend")
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_OK, response.getStatus());
		List<Long> recommended = response.readEntity(new GenericType<List<Long>>() {
		});
		// checking if a list was returned
		Assert.assertNotEquals("The given entity must not be null.", null, recommended);
		// checking if the list contains the right type
		Long[] arr = { new Long(1) };
		try {
			recommended.toArray(arr);
		} catch (ArrayStoreException e) {
			Assert.fail("The list should contain Long ids.");
		}

		// TEST RECOMMENDATION SINGLE
		// checking if sending null fails
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
				.request(MediaType.APPLICATION_JSON).post(Entity.entity(null, MediaType.APPLICATION_JSON));
		Assert.assertEquals(org.apache.catalina.connector.Response.SC_INTERNAL_SERVER_ERROR, response.getStatus());

		// test recommendation process is now available
		response = ClientBuilder.newBuilder().build()
				.target("http://localhost:3002/tools.descartes.petstore.recommender/rest/recommendsingle")
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
