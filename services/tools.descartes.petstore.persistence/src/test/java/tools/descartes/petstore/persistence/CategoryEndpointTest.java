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
package tools.descartes.petstore.persistence;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import tools.descartes.petstore.entities.Category;
import tools.descartes.petstore.persistence.rest.CategoryEndpoint;
import tools.descartes.petstore.persistence.domain.CategoryRepository;
import tools.descartes.petstore.rest.NonBalancedCRUDOperations;
import tools.descartes.petstore.rest.RESTClient;

/**
 * Test for the CategoryEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class CategoryEndpointTest {
	
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
		restServletConfig.register(CategoryEndpoint.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {	
		//open connection
		RESTClient<Category> client = new RESTClient<Category>("http://localhost:"
				+ getTomcatPort() + CONTEXT + "/", "rest", "categories", Category.class);
		int initialSize = NonBalancedCRUDOperations.getEntities(client, -1, -1).size();
		
		//create category
		Category cat = new Category();
		cat.setName("Category");
		cat.setDescription("Category Description");
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, cat);
		Assert.assertTrue(id >= 0);	
		
		//update category
		cat.setName("updatedCategory");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, cat));
		Assert.assertFalse(NonBalancedCRUDOperations.sendEntityForUpdate(client, 500L, cat));
		
		//receive category
		Category recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getName(), "updatedCategory");
		
		//create 1 additional category 
		cat.setName("test2");
		NonBalancedCRUDOperations.sendEntityForCreation(client, cat);
		
		//get some categories
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, 1, 1).size(), 1 + initialSize);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 2 + initialSize);
		
		//delete category
		NonBalancedCRUDOperations.deleteEntity(client, id);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 1 + initialSize);
		Assert.assertTrue(NonBalancedCRUDOperations.getEntities(client, -1, -1)
				.stream().noneMatch(te -> te.getId() == id));
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
