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
package tools.descartes.petsupplystore.persistence;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.persistence.rest.CacheManagerEndpoint;
import tools.descartes.petsupplystore.persistence.rest.DatabaseGenerationEndpoint;
import tools.descartes.petsupplystore.persistence.rest.ProductEndpoint;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.rest.NonBalancedCRUDOperations;
import tools.descartes.petsupplystore.rest.RESTClient;

/**
 * Test for the all federated cache utilities.
 * Also tests database generation endpoints.
 * @author Joakim von Kistowski
 *
 */
public class CacheTest {
	
	private TomcatTestHandler clientTomcatHandler;
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		clientTomcatHandler = new TomcatTestHandler(2, TomcatTestHandler.DEFAULT_TEST_TOMCAT_PORT,
				CacheManagerEndpoint.class, DatabaseGenerationEndpoint.class, ProductEndpoint.class);
	}
	
	/**
	 * Run the test.
	 * @throws Throwable on failure.
	 */
	@Test
	public void testEndpoint() throws Throwable {
		int client0Port = clientTomcatHandler.getTomcatPort(0);
		int client1Port = clientTomcatHandler.getTomcatPort(1);

		RESTClient<Product> p1c = new RESTClient<>("http://localhost:" 
				 + client0Port + "/" + Service.PERSISTENCE.getServiceName(),
				 "rest", "products", Product.class);
		RESTClient<Product> p2c = new RESTClient<>("http://localhost:" 
				 + client1Port + "/" + Service.PERSISTENCE.getServiceName(),
				 "rest", "products", Product.class);
		
		
		//create initial database
		RESTClient<String> dbc = new RESTClient<>("http://localhost:" 
				 + client0Port + "/" + Service.PERSISTENCE.getServiceName(),
				 "rest", "generatedb", String.class);
		Response response = dbc.getService()
				.path(dbc.getApplicationURI()).path(dbc.getEndpointURI())
				.queryParam("categories", 3)
				.queryParam("products", 10)
				.queryParam("users", 20)
				.queryParam("orders", 4)
				.request(MediaType.TEXT_PLAIN).get();
		//wait for database to finish generating and check if the flag is correct
		String finishedGenerating = dbc.getService()
				.path(dbc.getApplicationURI()).path(dbc.getEndpointURI()).path("finished")
				.request(MediaType.TEXT_PLAIN).get().readEntity(String.class);
		Assert.assertEquals("false", finishedGenerating);
		while (finishedGenerating.equals("false")) {
			Thread.sleep(2000);
			finishedGenerating = dbc.getService()
					.path(dbc.getApplicationURI()).path(dbc.getEndpointURI()).path("finished")
					.request(MediaType.TEXT_PLAIN).get().readEntity(String.class);
		}
		Assert.assertEquals("true", finishedGenerating);
		Assert.assertFalse(response.readEntity(String.class).isEmpty());
		
		//clear cache for Categories
		Response response2 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + client0Port + "/" + Service.PERSISTENCE.getServiceName() + "/rest/cache/class")
				.path(Category.class.getName()).request(MediaType.TEXT_PLAIN).delete();
		Assert.assertEquals(Category.class.getName(), response2.readEntity(String.class));
		
		Response response3 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + client1Port + "/" + Service.PERSISTENCE.getServiceName() + "/rest/cache/cache")
				.request(MediaType.TEXT_PLAIN).delete();
		Assert.assertEquals("cleared", response3.readEntity(String.class));
		
		long id = NonBalancedCRUDOperations.getEntities(p1c, -1, 1).get(0).getId();
		boolean deleted = NonBalancedCRUDOperations.deleteEntity(p2c, id);
		Assert.assertTrue(deleted);
		Product gone = NonBalancedCRUDOperations.getEntity(p1c, id);
		Assert.assertNull(gone);
		gone = NonBalancedCRUDOperations.getEntity(p2c, id);
		Assert.assertNull(gone);
		
	}
	
	/**
	 * Dismantles the embedded Tomcats.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@After
	public void dismantle() throws Throwable {
		clientTomcatHandler.dismantleAll();
	}
}
