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

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.persistence.rest.CategoryEndpoint;
import tools.descartes.petsupplystore.persistence.rest.ProductEndpoint;
import tools.descartes.petsupplystore.persistence.domain.CategoryRepository;
import tools.descartes.petsupplystore.rest.NonBalancedCRUDOperations;
import tools.descartes.petsupplystore.rest.RESTClient;

/**
 * Test for the ProductEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class ProductEndpointTest {
	
	private static final String CONTEXT = "/test";
	
	private Tomcat testTomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		testTomcat = new Tomcat();
		testTomcat.setPort(0);
		testTomcat.setBaseDir(testWorkingDir);
		Context context = testTomcat.addWebapp(CONTEXT, testWorkingDir);
		ResourceConfig restServletConfig = new ResourceConfig();
		restServletConfig.register(ProductEndpoint.class);
		restServletConfig.register(CategoryEndpoint.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
		System.out.println("Initializing Database with size " + CategoryRepository.REPOSITORY.getAllEntities().size());
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {
		//create category, replace with category endpoint once it is there
		RESTClient<Category> categoryClient = new RESTClient<Category>("http://localhost:"
		+ getTomcatPort() + CONTEXT + "/", "rest", "categories", Category.class);
		Category cat = new Category();
		cat.setName("Category");
		cat.setDescription("Category Description");
		long categoryId = NonBalancedCRUDOperations.sendEntityForCreation(categoryClient, cat);
		
		//open connection
		RESTClient<Product> client = new RESTClient<Product>("http://localhost:"
		+ getTomcatPort() + CONTEXT + "/", "rest", "products", Product.class);
		//get initial product table size
		int initialProducts = NonBalancedCRUDOperations.getEntities(client, -1, -1).size();
		
		
		//create product
		Product creationProduct = new Product();
		creationProduct.setCategoryId(categoryId);
		creationProduct.setName("rest product");
		creationProduct.setDescription("rest description");
		creationProduct.setListPriceInCents(999);
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		Assert.assertTrue(id >= 0);
		
		//create invalid product
		creationProduct.setCategoryId(-2L);
		long noid = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		Assert.assertTrue(noid <= 0);
		
		//update product
		creationProduct.setName("updated");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, creationProduct));
		Assert.assertFalse(NonBalancedCRUDOperations.sendEntityForUpdate(client, -1L, creationProduct));
		
		//receive product
		Product recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getName(), "updated");
		
		//create 2 additional products
		creationProduct.setCategoryId(categoryId);
		creationProduct.setName("test2");
		long id2 = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		creationProduct.setName("test3");
		NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		
		//get some products
		List<Product> products = NonBalancedCRUDOperations.getEntities(client, 1, 2);
		Assert.assertEquals(products.size(), 2);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 3 + initialProducts);
		
		//get products with category
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, "category", categoryId, -1, -1).size(), 3);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, "category", 99999, -1, -1).size(), 0);
		Assert.assertEquals("3", client.getEndpointTarget().path("count").path("" + categoryId)
				.request(MediaType.TEXT_PLAIN).get().readEntity(String.class));
		Assert.assertEquals(404, client.getEndpointTarget().path("count").path("99999")
				.request(MediaType.TEXT_PLAIN).get().getStatus());
		
		//delete product
		NonBalancedCRUDOperations.deleteEntity(client, id2);
		products = NonBalancedCRUDOperations.getEntities(client, -1, -1);
		Assert.assertTrue(products.size() >= 2 && products.stream().noneMatch(te -> te.getId() == id2));
	}
	
	/**
	 * Dismantles the embedded Tomcat.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@After
	public void dismantle() throws Throwable {
		if (testTomcat.getServer() != null && testTomcat.getServer().getState() != LifecycleState.DESTROYED) {
	        if (testTomcat.getServer().getState() != LifecycleState.STOPPED) {
	        	testTomcat.stop();
	        }
	        testTomcat.destroy();
	    }
	}
	
	private int getTomcatPort() {
		return testTomcat.getConnector().getLocalPort();
	}
	
}
