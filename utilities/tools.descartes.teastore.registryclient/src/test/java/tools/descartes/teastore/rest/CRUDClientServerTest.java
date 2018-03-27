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
package tools.descartes.teastore.rest;

import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;

/**
 * Test for CRUDClient and AbstractCrudEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class CRUDClientServerTest {
	
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
		restServletConfig.register(TestEntityEndpoint.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
	}
	
	/**
	 * Tests the CRUDCLient.
	 */
	@Test
	public void testCRUDClient() {
		RESTClient<TestEntity> client =
				new RESTClient<>("http://localhost:" + getTomcatPort() + CONTEXT + "/", "rest", "testentities",
						TestEntity.class);
		TestEntity sendEnt = new TestEntity();
		sendEnt.setAttribute("test");
		sendEnt.setId(-2L);
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, sendEnt);
		Assert.assertTrue(id >= 0);
		sendEnt.setAttribute("test0");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, sendEnt));
		try {
			NonBalancedCRUDOperations.sendEntityForUpdate(client, -1L, sendEnt);
			Assert.fail();
		} catch (NotFoundException e) {
			//dont't fail
		}
		TestEntity recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getAttribute(), "test0");
	
		sendEnt.setAttribute("test2");
		long id2 = NonBalancedCRUDOperations.sendEntityForCreation(client, sendEnt);
		sendEnt.setAttribute("test3");
		NonBalancedCRUDOperations.sendEntityForCreation(client, sendEnt);
		List<TestEntity> products = NonBalancedCRUDOperations.getEntities(client, 1, 2);
		Assert.assertTrue(products.size() == 2);
		NonBalancedCRUDOperations.deleteEntity(client, id2);
		products = NonBalancedCRUDOperations.getEntities(client, -1, -1);
		Assert.assertTrue(products.size() == 2 && products.stream().noneMatch(te -> te.getId() == id2));
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
