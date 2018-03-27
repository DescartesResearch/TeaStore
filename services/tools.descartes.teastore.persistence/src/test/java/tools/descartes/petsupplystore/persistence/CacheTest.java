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
package tools.descartes.teastore.persistence;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.persistence.rest.CacheManagerEndpoint;
import tools.descartes.teastore.persistence.rest.DatabaseGenerationEndpoint;
import tools.descartes.teastore.persistence.rest.ProductEndpoint;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Product;

/**
 * Test for the all federated cache utilities.
 * Also tests database generation endpoints.
 * @author Joakim von Kistowski
 *
 */
public class CacheTest {
	
	private static final int MOCK_REGISTRY_PORT = 42999;
	
	
	private TomcatTestHandler clientTomcatHandler;
	/**
	 * Wiremock Rule for the local mock registry.
	 */
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(MOCK_REGISTRY_PORT);
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		clientTomcatHandler = new TomcatTestHandler(2, TomcatTestHandler.DEFAULT_TEST_TOMCAT_PORT,
				wireMockRule,
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

		RESTClient<Product> p0c = new RESTClient<>("http://localhost:" 
				 + client0Port + "/" + Service.PERSISTENCE.getServiceName(),
				 "rest", "products", Product.class);
		RESTClient<Product> p1c = new RESTClient<>("http://localhost:" 
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
		
		long id = NonBalancedCRUDOperations.getEntities(p0c, -1, 1).get(0).getId();
		long id2 = NonBalancedCRUDOperations.getEntities(p1c, -1, 1).get(0).getId();
		Assert.assertEquals(id, id2);
		
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
