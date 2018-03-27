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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.teastore.persistence.domain.CategoryRepository;
import tools.descartes.teastore.persistence.repository.EMFManagerInitializer;
import tools.descartes.teastore.registryclient.Service;

/**
 * Creates and manages an embedded Tomcat for testing.
 * @author Jóakim von Kistowski
 *
 */
public class TomcatTestHandler {

	/**
	 * Default context for testing webapp.
	 */
	public static final String CONTEXT = "/" + Service.PERSISTENCE.getServiceName();
	/**
	 * Default Port for the Testing Tomcat.
	 */
	public static final int DEFAULT_TEST_TOMCAT_PORT = 43001;
	
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	private Tomcat[] tomcats;
	
	/**
	 * Create a Tomcat test handler for persistence testing.
	 * @param count Number of testing tomcats.
	 * @param startPort Port to start with (do not use 0 for auto-assigning).
	 * @param wireMockRule Wire mock rule for mocking the registry.The test handler will
	 * add all services with respective stubs to the rule.
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public TomcatTestHandler(int count, int startPort, WireMockRule wireMockRule, Class<?>... endpoints)
			throws ServletException, LifecycleException, JsonProcessingException {
		tomcats = new Tomcat[count];
		EMFManagerInitializer.initializeEMF();
		for (int i = 0; i < count; i++) {
			tomcats[i] = new Tomcat();
			tomcats[i].setPort(startPort + i);
			tomcats[i].setBaseDir(testWorkingDir);
			Context context = tomcats[i].addWebapp(CONTEXT, testWorkingDir);
			//Registry
			if (wireMockRule != null) {
				ContextEnvironment registryURL = new ContextEnvironment();
				registryURL.setDescription("");
				registryURL.setOverride(false);
				registryURL.setType("java.lang.String");
				registryURL.setName("registryURL");
				registryURL.setValue("http://localhost:" + wireMockRule.port()
				+ "/tools.descartes.teastore.registry/rest/services/");
				context.getNamingResources().addEnvironment(registryURL);
				ContextEnvironment servicePort = new ContextEnvironment();
				servicePort.setDescription("");
				servicePort.setOverride(false);
				servicePort.setType("java.lang.String");
			    servicePort.setName("servicePort");
			    servicePort.setValue("" + startPort + i);
				context.getNamingResources().addEnvironment(servicePort);
				context.addApplicationListener(RegistrationDaemon.class.getName());
			}
			//REST endpoints
			ResourceConfig restServletConfig = new ResourceConfig();
			for (Class<?> endpoint: endpoints) {
				restServletConfig.register(endpoint);
			}
			ServletContainer restServlet = new ServletContainer(restServletConfig);
			tomcats[i].addServlet(CONTEXT, "restServlet", restServlet);
			context.addServletMappingDecoded("/rest/*", "restServlet");
			tomcats[i].start();
		}
		if (wireMockRule != null) {
			initializeMockRegistry(wireMockRule, count, startPort);
		}
		System.out.println("Initializing Database with size " + CategoryRepository.REPOSITORY.getAllEntities().size());
	}
	
	/**
	 * Create a Tomcat test handler. Expects no active registry to be mocked.
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public TomcatTestHandler(Class<?>... endpoints)
			throws ServletException, LifecycleException, JsonProcessingException {
		this(1, DEFAULT_TEST_TOMCAT_PORT, null, endpoints);
	}
	
	private void initializeMockRegistry(WireMockRule wireMockRule, int count, int startport)
			throws JsonProcessingException {
		List<String> strings = new LinkedList<String>();
		for (int i = 0; i < count; i++) {
			strings.add("localhost:" + (startport + i));
		}
		String json = new ObjectMapper().writeValueAsString(strings);
		wireMockRule.stubFor(WireMock.get(WireMock.urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/" + Service.PERSISTENCE.getServiceName()))
						.willReturn(WireMock.okJson(json)));
		wireMockRule.stubFor(WireMock.post(WireMock.urlEqualTo(
				"/tools.descartes.teastore.registry/rest/services/"
						+ Service.PERSISTENCE.getServiceName() + "/*"))
		.willReturn(WireMock.ok()));
	}
	
	/**
	 * Get the first (usually only) Tomcat.
	 * @return The tomcat.
	 */
	public Tomcat getTomcat() {
		return tomcats[0];
	}
	
	/**
	 * Get the nth Tomcat.
	 * @param index The tomcat index.
	 * @return The tomcat.
	 */
	public Tomcat getTomcat(int index) {
		return tomcats[index];
	}
	
	/**
	 * Get the first (usually only) Tomcat's port.
	 * @return The port.
	 */
	public int getTomcatPort() {
		return getTomcat().getConnector().getLocalPort();
	}
	
	/**
	 * Get the nth Tomcat's port.
	 * @param index The tomcat index.
	 * @return The port.
	 */
	public int getTomcatPort(int index) {
		return getTomcat(index).getConnector().getLocalPort();
	}
	
	/**
	 * Dismantle all Tomcats.
	 */
	public void dismantleAll() {
		for (int i = 0; i < tomcats.length; i++) {
			dismantle(i);
		}
	}
	
	/**
	 * Dismantle Tomcat with index.
	 * @param index The tomcat index.
	 */
	public void dismantle(int index) {
		try {
			if (tomcats[index].getServer() != null
					&& tomcats[index].getServer().getState() != LifecycleState.DESTROYED) {
		        if (tomcats[index].getServer().getState() != LifecycleState.STOPPED) {
		        	tomcats[index].stop();
		        }
		        tomcats[index].destroy();
		    }
		} catch (LifecycleException e) {
			System.out.println("Exception shutting down Testing Tomcat (this may happen a lot): " + e.getMessage());
		}
	}
}
