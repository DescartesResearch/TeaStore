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

import tools.descartes.petstore.entities.User;
import tools.descartes.petstore.persistence.rest.UserEndpoint;
import tools.descartes.petstore.persistence.domain.CategoryRepository;
import tools.descartes.petstore.rest.NonBalancedCRUDOperations;
import tools.descartes.petstore.rest.RESTClient;

/**
 * Test for the UserEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class UserEndpointTest {
	
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
		restServletConfig.register(UserEndpoint.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
		System.out.println("Started Testing Tomcat at port " + getTomcatPort());
		int dbinitializationSize = CategoryRepository.REPOSITORY.getAllEntities().size();
		System.out.println("DB initialized at size " + dbinitializationSize);
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {	
		//open connection
		RESTClient<User> client = new RESTClient<User>("http://localhost:"
				+ getTomcatPort() + CONTEXT + "/", "rest", "users", User.class);
		int initialSize = NonBalancedCRUDOperations.getEntities(client, -1, -1).size();
		
		//create user
		User user = new User();
		user.setEmail("email");
		user.setRealName("realname");
		user.setUserName("username200");
		user.setPassword("password");
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, user);
		Assert.assertTrue(id >= 0);
		
		//update user
		user.setRealName("updated");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, user));
		Assert.assertFalse(NonBalancedCRUDOperations.sendEntityForUpdate(client, 50000L, user));
		
		//receive user
		User recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getRealName(), "updated");
		
		//create 1 additional user 
		user.setUserName("test2");
		NonBalancedCRUDOperations.sendEntityForCreation(client, user);
		
		//get some users
		Assert.assertEquals(1, NonBalancedCRUDOperations.getEntities(client, 1, 1).size());
		Assert.assertEquals(2 + initialSize, NonBalancedCRUDOperations.getEntities(client, -1, -1).size());
		Assert.assertEquals(id,
				NonBalancedCRUDOperations.getEntityWithProperty(client, "name", "username200").getId());
		//create invalid user
		long invalidId = NonBalancedCRUDOperations.sendEntityForCreation(client, user);
		Assert.assertTrue(invalidId < 0);
		
		//delete user
		NonBalancedCRUDOperations.deleteEntity(client, id);
		Assert.assertEquals(1 + initialSize, NonBalancedCRUDOperations.getEntities(client, -1, -1).size());
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
