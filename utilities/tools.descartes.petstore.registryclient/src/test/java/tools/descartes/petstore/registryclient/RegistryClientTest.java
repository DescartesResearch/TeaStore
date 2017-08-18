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
package tools.descartes.petstore.registryclient;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.petstore.registry.rest.Registry;
import tools.descartes.petstore.registry.rest.RegistryREST;

/**
 * Test for the Registry.
 * @author Simon Eismann
 *
 */
public class RegistryClientTest {
	
	private static final String CONTEXT = "/test";
	private static int clientPort = 40000;
	
	private Tomcat registryTomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		registryTomcat = new Tomcat();
		registryTomcat.setPort(3000);
		registryTomcat.setBaseDir(testWorkingDir);
		Context context = registryTomcat.addWebapp(CONTEXT, testWorkingDir);
		ResourceConfig restServletConfig = new ResourceConfig();
		restServletConfig.register(RegistryREST.class);
		restServletConfig.register(Registry.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		registryTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		registryTomcat.start();
		System.out.println("Started Registry Tomcat at port " + getRegistryPort());
	}

	/**
	 * Run the test.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 * @throws InterruptedException Exception on failure.
	 */
	@Test
	public void test() throws ServletException, LifecycleException, InterruptedException {
		Tomcat store1 = new Tomcat();
		createClientTomcat(Service.STORE, store1);
		waitAndValidate(Service.STORE, new String[] {"40000"});
		Tomcat store2 = new Tomcat();
		createClientTomcat(Service.STORE, store2);
		waitAndValidate(Service.STORE, new String[] {"40000", "40001"});
		store2.stop();
		waitAndValidate(Service.STORE, new String[] {"40000"});
		Tomcat recommender = new Tomcat();
		createClientTomcat(Service.RECOMMENDER, recommender);
		waitAndValidate(Service.RECOMMENDER, new String[] {"40002"});
		waitAndValidate(Service.STORE, new String[] {"40000"});
	}
	
	private void waitAndValidate(Service service, String[] strings) throws InterruptedException {
		Thread.sleep(10000);
		Response response = ClientBuilder.newBuilder().build().target("http://localhost:"
				+ getRegistryPort() + "/test/rest/services/" + service.getServiceName())
				.request(MediaType.APPLICATION_JSON).get();
		List<String> list = response.readEntity(new GenericType<List<String>>() { }); 
		Assert.assertEquals(strings.length, list.size());
		List<String> expectedList = Arrays.asList(strings);
		for (String result: list) {
			Assert.assertTrue("Resulting list contained unexpectedly: "
					+ result.split(":")[1], expectedList.contains(result.split(":")[1]));
		}
		
	}

	private Tomcat createClientTomcat(Service service, Tomcat tomcat) throws ServletException, LifecycleException {
		int clientPort = getNextClientPort();
		tomcat.getEngine().setName("Catalina" + clientPort);
		tomcat.setPort(clientPort);
		tomcat.setBaseDir(testWorkingDir);
		tomcat.enableNaming();
		Context context = tomcat.addWebapp("/" + service.getServiceName() + clientPort, testWorkingDir);
		ContextEnvironment registryURL = new ContextEnvironment();
		registryURL.setDescription("");
		registryURL.setOverride(false);
		registryURL.setType("java.lang.String");
		registryURL.setName("registryURL");
		registryURL.setValue("http://localhost:" + getRegistryPort() + "/test/rest/services/");
		context.getNamingResources().addEnvironment(registryURL);
		ContextEnvironment servicePort = new ContextEnvironment();
		servicePort.setDescription("");
		servicePort.setOverride(false);
		servicePort.setType("java.lang.String");
	    servicePort.setName("servicePort");
	    servicePort.setValue("" + clientPort);
		context.getNamingResources().addEnvironment(servicePort);		
		context.addApplicationListener(TestRegistryClientStartup.class.getName());
		ResourceConfig restServletConfig = new ResourceConfig();
		ServletContainer restServlet2 = new ServletContainer(restServletConfig);
		tomcat.addServlet("/" + service.getServiceName() + clientPort, "restServlet", restServlet2);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		tomcat.start();
		return tomcat;
	}
	
	private int getNextClientPort() {
		return clientPort++;
	}

	/**
	 * Dismantles the embedded Tomcat.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@After
	public void dismantle() throws Throwable {
		if (registryTomcat.getServer() != null && registryTomcat.getServer().getState() != LifecycleState.DESTROYED) {
	        if (registryTomcat.getServer().getState() != LifecycleState.STOPPED) {
	        	registryTomcat.stop();
	        }
	        registryTomcat.destroy();
	    }
		Registry.getRegistryInstance().getHeartbeatMap().clear();
		Registry.getRegistryInstance().getMap().clear();
	}
	
	private int getRegistryPort() {
		return registryTomcat.getConnector().getLocalPort();
	}
	
}