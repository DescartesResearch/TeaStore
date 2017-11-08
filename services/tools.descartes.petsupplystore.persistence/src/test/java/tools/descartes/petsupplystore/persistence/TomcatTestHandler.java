package tools.descartes.petsupplystore.persistence;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

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
import org.junit.Rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import tools.descartes.petsupplystore.persistence.domain.CategoryRepository;
import tools.descartes.petsupplystore.persistence.repository.EMFManagerInitializer;
import tools.descartes.petsupplystore.registryclient.Service;

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
	 * Port of the mock registry.
	 */
	public static final int MOCK_REGISTRY_PORT = 43000;
	/**
	 * Default Port for the Testing Tomcat.
	 */
	public static final int DEFAULT_TEST_TOMCAT_PORT = 43001;
	
	@Rule
	private WireMockRule wireMockRule = new WireMockRule(MOCK_REGISTRY_PORT);
	
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	private Tomcat[] tomcats;
	
	/**
	 * Create a Tomcat test handler for persistence testing.
	 * @param count Number of testing tomcats.
	 * @param startPort Port to start with (do not use 0 for auto-assigning).
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public TomcatTestHandler(int count, int startPort, Class<?>... endpoints)
			throws ServletException, LifecycleException, JsonProcessingException {
		tomcats = new Tomcat[count];
		EMFManagerInitializer.initializeEMF();
		for (int i = 0; i < count; i++) {
			tomcats[i] = new Tomcat();
			tomcats[i].setPort(startPort + i);
			tomcats[i].setBaseDir(testWorkingDir);
			Context context = tomcats[i].addWebapp(CONTEXT, testWorkingDir);
			//Registry
			ContextEnvironment registryURL = new ContextEnvironment();
			registryURL.setDescription("");
			registryURL.setOverride(false);
			registryURL.setType("java.lang.String");
			registryURL.setName("registryURL");
			registryURL.setValue("http://localhost:" + MOCK_REGISTRY_PORT + "/test/rest/services/");
			context.getNamingResources().addEnvironment(registryURL);
			ContextEnvironment servicePort = new ContextEnvironment();
			servicePort.setDescription("");
			servicePort.setOverride(false);
			servicePort.setType("java.lang.String");
		    servicePort.setName("servicePort");
		    servicePort.setValue("" + startPort + i);
			context.getNamingResources().addEnvironment(servicePort);	
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
		initializeMockRegistry(count, startPort);
		System.out.println("Initializing Database with size " + CategoryRepository.REPOSITORY.getAllEntities().size());
	}
	
	/**
	 * Create a Tomcat test handler.
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 * @throws JsonProcessingException Exception on failure.
	 */
	public TomcatTestHandler(Class<?>... endpoints)
			throws ServletException, LifecycleException, JsonProcessingException {
		this(1, DEFAULT_TEST_TOMCAT_PORT, endpoints);
	}
	
	private void initializeMockRegistry(int count, int startport) throws JsonProcessingException {
		List<String> strings = new LinkedList<String>();
		for (int i = 0; i < count; i++) {
			strings.add("localhost:" + (startport + i));
		}
		String json = new ObjectMapper().writeValueAsString(strings);
		wireMockRule.stubFor(get(urlEqualTo(
				"/tools.descartes.petsupplystore.registry/rest/services/" + Service.PERSISTENCE.getServiceName() + "/"))
						.willReturn(okJson(json)));
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
