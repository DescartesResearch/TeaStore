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
package tools.descartes.teastore.registry;
/**
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

import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.teastore.registry.rest.Registry;
import tools.descartes.teastore.registry.rest.RegistryREST;

/**
 * Test for the Registry.
 * @author Simon Eismann
 *
 */
public class RegistryTest {
	
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
		restServletConfig.register(RegistryREST.class);
		restServletConfig.register(Registry.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
	}

	/**
	 * Test if an empty registry returns an empty server list.
	 */
	@Test
	public void testGetEmpty() {
		 Response response = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service1").request(MediaType.APPLICATION_JSON).get();
		 Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
		 List<String> list = response.readEntity(new GenericType<List<String>>() { }); 
		 Assert.assertTrue(list != null);
		 Assert.assertTrue(list.size() == 0);
	}
	
	/**
	 * Test if after registration of service they can be found in registry.
	 */
	@Test
	public void testRegister() {
		 Response response1 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service1/abbaasd")
				 .request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		 Assert.assertTrue(response1.getStatus() == Response.Status.CREATED.getStatusCode());
		 Response response2 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service1/abbaasd2")
				 .request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		 Assert.assertTrue(response2.getStatus() == Response.Status.CREATED.getStatusCode());
		 Response response = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service1").request(MediaType.APPLICATION_JSON).get();
		 Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
		 List<String> list = response.readEntity(new GenericType<List<String>>() { }); 
		 Assert.assertTrue(list != null);
		 Assert.assertTrue(list.size() == 2);
		 Assert.assertTrue(list.get(0).equals("abbaasd"));
		 Assert.assertTrue(list.get(1).equals("abbaasd2"));
	}
	
	/**
	 * Test if unregistering a service actually removes it from the registry.
	 */
	@Test
	public void testUnregisterSuccess() {
		 Response response1 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service2/abbaasd")
				 .request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		 Assert.assertTrue(response1.getStatus() == Response.Status.CREATED.getStatusCode());
		 Response response2 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service2/abbaasd")
				 .request(MediaType.APPLICATION_JSON).delete();
		 Assert.assertTrue(response2.getStatus() == Response.Status.OK.getStatusCode());
		 Response response = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service2").request(MediaType.APPLICATION_JSON).get();
		 Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
		 List<String> list = response.readEntity(new GenericType<List<String>>() { }); 
		 Assert.assertTrue(list != null);
		 Assert.assertTrue(list.size() == 0);
	}
	
	/**
	 * Test if unregistering fails if service was never registered.
	 */
	@Test
	public void testUnregisterFail() {
		 Response response = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service3/abbaasd")
				 .request(MediaType.APPLICATION_JSON).delete();
		 Assert.assertTrue(response.getStatus() == Response.Status.NOT_FOUND.getStatusCode());
	}

	/**
	 * Test if registering fails if service is already registered.
	 */
	@Test
	public void testRegisterFail() {
		 Response response1 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service4/abbaasda")
				 .request(MediaType.APPLICATION_JSON).put(Entity.text(""));
		 Assert.assertTrue(response1.getStatus() == Response.Status.CREATED.getStatusCode());
		 Response response2 = ClientBuilder.newBuilder().build().target("http://localhost:" 
				 + getTomcatPort() + "/test/rest/services/service4/abbaasda")
				 .request(MediaType.APPLICATION_JSON).put(Entity.text(""));
	     Assert.assertTrue(response2.getStatus() == Response.Status.OK.getStatusCode());
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
