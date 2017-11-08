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
package tools.descartes.petsupplystore.recommender;

import java.util.Arrays;

import javax.ws.rs.client.ClientBuilder;
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

import tools.descartes.petsupplystore.recommender.rest.RecommendEndpoint;
import tools.descartes.petsupplystore.recommender.rest.RecommendSingleEndpoint;
import tools.descartes.petsupplystore.recommender.rest.TrainEndpoint;
import tools.descartes.petsupplystore.recommender.servlet.RecommenderStartup;
import tools.descartes.petsupplystore.registryclient.Service;

/**
 * Abstract base for testing of the stores rest functionality.
 * 
 * @author Simon Eismann
 *
 */
public abstract class AbstractRecommenderRestTest {

	/**
	 * Port for testing recommender.
	 */
	protected static final int RECOMMENDER_TEST_PORT = 3002;
	private MockPersistenceProvider persistence;
	private MockRegistry registry;
	private Tomcat testTomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");

	/**
	 * Sets up a registry, a persistance unit and a store.
	 * 
	 * @throws Throwable
	 *             Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		persistence = new MockPersistenceProvider();
		registry = new MockRegistry(Arrays.asList(persistence.getPort()),
				Arrays.asList(RECOMMENDER_TEST_PORT));
		
		//debuggging response 
		Response response0 = ClientBuilder.newBuilder().build()
				.target("http://localhost:" + MockRegistry.MOCK_REGISTRY_PORT
				+ "/tools.descartes.petsupplystore.registry/rest/services/" + Service.PERSISTENCE.getServiceName())
				.request(MediaType.APPLICATION_JSON).get();
		System.out.println(response0.getStatus());
		
		// Setup registry
//		registryTomcat = new Tomcat();
//		registryTomcat.setPort(3000);
//		registryTomcat.setBaseDir(testWorkingDir);
//		Context context = registryTomcat.addWebapp(CONTEXT, testWorkingDir);
//		ResourceConfig restServletConfig = new ResourceConfig();
//		restServletConfig.register(RegistryREST.class);
//		restServletConfig.register(Registry.class);
//		ServletContainer restServlet = new ServletContainer(restServletConfig);
//		registryTomcat.addServlet(CONTEXT, "restServlet", restServlet);
//		context.addServletMappingDecoded("/rest/*", "restServlet");
//		registryTomcat.start();

		// Setup persistance tomcat
//		persistanceTomcat = new Tomcat();
//		persistanceTomcat.setPort(3001);
//		persistanceTomcat.setBaseDir(testWorkingDir);
//		persistanceTomcat.enableNaming();
//		Context context2 = persistanceTomcat.addWebapp("/tools.descartes.petsupplystore.persistence", testWorkingDir);
//		ContextEnvironment registryURL = new ContextEnvironment();
//		registryURL.setDescription("");
//		registryURL.setOverride(false);
//		registryURL.setType("java.lang.String");
//		registryURL.setName("registryURL");
//		registryURL.setValue("http://localhost:3000/test/rest/services/");
//		context2.getNamingResources().addEnvironment(registryURL);
//		ContextEnvironment servicePort = new ContextEnvironment();
//		servicePort.setDescription("");
//		servicePort.setOverride(false);
//		servicePort.setType("java.lang.String");
//		servicePort.setName("servicePort");
//		servicePort.setValue("3001");
//		context2.getNamingResources().addEnvironment(servicePort);
//		ResourceConfig restServletConfig2 = new ResourceConfig();
//		restServletConfig2.register(CategoryEndpoint.class);
//		restServletConfig2.register(OrderEndpoint.class);
//		restServletConfig2.register(OrderItemEndpoint.class);
//		restServletConfig2.register(ProductEndpoint.class);
//		restServletConfig2.register(UserEndpoint.class);
//		ServletContainer restServlet2 = new ServletContainer(restServletConfig2);
//		persistanceTomcat.addServlet("/tools.descartes.petsupplystore.persistence", "restServlet", restServlet2);
//		context2.addServletMappingDecoded("/rest/*", "restServlet");
//		context2.addApplicationListener(InitialDataGenerationDaemon.class.getName());
//		persistanceTomcat.start();

		// Setup recommend tomcat
		testTomcat = new Tomcat();
		testTomcat.setPort(RECOMMENDER_TEST_PORT);
		testTomcat.setBaseDir(testWorkingDir);
		testTomcat.enableNaming();
		Context context3 = testTomcat.addWebapp("/" + Service.RECOMMENDER.getServiceName(), testWorkingDir);
		ContextEnvironment registryURL3 = new ContextEnvironment();
		registryURL3.setDescription("");
		registryURL3.setOverride(false);
		registryURL3.setType("java.lang.String");
		registryURL3.setName("registryURL");
		registryURL3.setValue("http://localhost:" + registry.getPort()
		+ "/tools.descartes.petsupplystore.registry/rest/services/");
		context3.getNamingResources().addEnvironment(registryURL3);
		ContextEnvironment servicePort3 = new ContextEnvironment();
		servicePort3.setDescription("");
		servicePort3.setOverride(false);
		servicePort3.setType("java.lang.String");
		servicePort3.setName("servicePort");
		servicePort3.setValue("" + RECOMMENDER_TEST_PORT);
		context3.getNamingResources().addEnvironment(servicePort3);
		ResourceConfig restServletConfig3 = new ResourceConfig();
		restServletConfig3.register(TrainEndpoint.class);
		restServletConfig3.register(RecommendEndpoint.class);
		restServletConfig3.register(RecommendSingleEndpoint.class);
		ServletContainer restServlet3 = new ServletContainer(restServletConfig3);
		testTomcat.addServlet("/" + Service.RECOMMENDER.getServiceName(), "restServlet", restServlet3);
		context3.addServletMappingDecoded("/rest/*", "restServlet");
		context3.addApplicationListener(RecommenderStartup.class.getName());
		testTomcat.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Dismantle Tomcat.
	 */
	@After
	public void dismantle() {
		try {
			if (testTomcat.getServer() != null
					&& testTomcat.getServer().getState() != LifecycleState.DESTROYED) {
		        if (testTomcat.getServer().getState() != LifecycleState.STOPPED) {
		        	testTomcat.stop();
		        }
		        testTomcat.destroy();
		    }
		} catch (LifecycleException e) {
			System.out.println("Exception shutting down testing Tomcat: " + e.getMessage());
		}
	}

}
