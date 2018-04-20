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
package tools.descartes.teastore.recommender;

import java.util.Arrays;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.recommender.rest.RecommendEndpoint;
import tools.descartes.teastore.recommender.rest.RecommendSingleEndpoint;
import tools.descartes.teastore.recommender.rest.TrainEndpoint;
import tools.descartes.teastore.recommender.servlet.RecommenderStartup;
import tools.descartes.teastore.registryclient.Service;

/**
 * Abstract base for testing of the recommender rest functionality.
 * 
 * @author Johannes Grohmann
 *
 */
public abstract class AbstractRecommenderRestTest {

	/**
	 * Port for testing recommender.
	 */
	protected static final int RECOMMENDER_TEST_PORT = 3002;

	/**
	 * The wiremock rule for the mock persistence.
	 */
	@Rule
	public WireMockRule persistenceWireMockRule = new WireMockRule(
			MockPersistenceProvider.DEFAULT_MOCK_PERSISTENCE_PORT);
	private MockPersistenceProvider persistence;

	/**
	 * The wiremock rule for the mock second recommender.
	 */
	@Rule
	public WireMockRule otherRecommenderWireMockRule = new WireMockRule(
			MockOtherRecommenderProvider.DEFAULT_MOCK_RECOMMENDER_PORT);
	private MockOtherRecommenderProvider otherrecommender;

	/**
	 * The wiremock rule for the mock registry.
	 */
	@Rule
	public WireMockRule registryWireMockRule = new WireMockRule(MockRegistry.DEFAULT_MOCK_REGISTRY_PORT);
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
		persistence = new MockPersistenceProvider(persistenceWireMockRule);

		otherrecommender = new MockOtherRecommenderProvider(otherRecommenderWireMockRule);

		setRegistry(new MockRegistry(registryWireMockRule, Arrays.asList(getPersistence().getPort()),
				Arrays.asList(RECOMMENDER_TEST_PORT, otherrecommender.getPort())));

		// debuggging response
		// Response response1 = ClientBuilder
		// .newBuilder().build().target("http://localhost:" + otherrecommender.getPort()
		// + "/"
		// + Service.RECOMMENDER.getServiceName() + "/rest/train/timestamp")
		// .request(MediaType.APPLICATION_JSON).get();
		// System.out.println(response1.getStatus() + ":" +
		// response1.readEntity(String.class));

		// debuggging response
		// Response response0 = ClientBuilder.newBuilder().build()
		// .target("http://localhost:" + MockRegistry.DEFAULT_MOCK_REGISTRY_PORT
		// + "/tools.descartes.teastore.registry/rest/services/"
		// + Service.PERSISTENCE.getServiceName() + "/")
		// .request(MediaType.APPLICATION_JSON).get();
		// System.out.println(response0.getStatus() + ":" +
		// response0.readEntity(String.class));
		//
		// Response response1 = ClientBuilder.newBuilder().build()
		// .target("http://localhost:" + persistence.getPort()
		// + "/tools.descartes.teastore.persistence/rest/orderitems")
		// .request(MediaType.APPLICATION_JSON).get();
		// System.out.println(response1.getStatus() + ":" +
		// response1.readEntity(String.class));

		// Setup recommend tomcat
		testTomcat = new Tomcat();
		testTomcat.setPort(RECOMMENDER_TEST_PORT);
		testTomcat.setBaseDir(testWorkingDir);
		testTomcat.enableNaming();
		Context context = testTomcat.addWebapp("/" + Service.RECOMMENDER.getServiceName(), testWorkingDir);
		ContextEnvironment registryURL3 = new ContextEnvironment();
		registryURL3.setDescription("");
		registryURL3.setOverride(false);
		registryURL3.setType("java.lang.String");
		registryURL3.setName("registryURL");
		registryURL3.setValue(
				"http://localhost:" + registry.getPort() + "/tools.descartes.teastore.registry/rest/services/");
		context.getNamingResources().addEnvironment(registryURL3);
		ContextEnvironment servicePort3 = new ContextEnvironment();
		servicePort3.setDescription("");
		servicePort3.setOverride(false);
		servicePort3.setType("java.lang.String");
		servicePort3.setName("servicePort");
		servicePort3.setValue("" + RECOMMENDER_TEST_PORT);
		context.getNamingResources().addEnvironment(servicePort3);
		ResourceConfig restServletConfig3 = new ResourceConfig();
		restServletConfig3.register(TrainEndpoint.class);
		restServletConfig3.register(RecommendEndpoint.class);
		restServletConfig3.register(RecommendSingleEndpoint.class);
		ServletContainer restServlet3 = new ServletContainer(restServletConfig3);
		testTomcat.addServlet("/" + Service.RECOMMENDER.getServiceName(), "restServlet", restServlet3);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		context.addApplicationListener(RecommenderStartup.class.getName());

		ContextEnvironment recommender = new ContextEnvironment();
		recommender.setDescription("");
		recommender.setOverride(false);
		recommender.setType("java.lang.String");
		recommender.setName("recommenderAlgorithm");
		recommender.setValue("PreprocessedSlopeOne");
		context.getNamingResources().addEnvironment(recommender);

		ContextEnvironment retrainlooptime = new ContextEnvironment();
		retrainlooptime.setDescription("");
		retrainlooptime.setOverride(false);
		retrainlooptime.setType("java.lang.Long");
		retrainlooptime.setName("recommenderLoopTime");
		retrainlooptime.setValue("100");
		context.getNamingResources().addEnvironment(retrainlooptime);

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
			if (testTomcat.getServer() != null && testTomcat.getServer().getState() != LifecycleState.DESTROYED) {
				if (testTomcat.getServer().getState() != LifecycleState.STOPPED) {
					testTomcat.stop();
				}
				testTomcat.destroy();
			}
		} catch (LifecycleException e) {
			System.out.println("Exception shutting down testing Tomcat: " + e.getMessage());
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the persistence
	 */
	public MockPersistenceProvider getPersistence() {
		return persistence;
	}

	/**
	 * @param persistence
	 *            the persistence to set
	 */
	public void setPersistence(MockPersistenceProvider persistence) {
		this.persistence = persistence;
	}

	/**
	 * @return the registry
	 */
	public MockRegistry getRegistry() {
		return registry;
	}

	/**
	 * @param registry
	 *            the registry to set
	 */
	public void setRegistry(MockRegistry registry) {
		this.registry = registry;
	}

}
