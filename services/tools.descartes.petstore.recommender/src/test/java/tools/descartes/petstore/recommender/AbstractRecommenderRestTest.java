package tools.descartes.petstore.recommender;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Before;

import tools.descartes.petstore.persistence.daemons.InitialDataGenerationDaemon;
import tools.descartes.petstore.persistence.rest.CategoryEndpoint;
import tools.descartes.petstore.persistence.rest.OrderEndpoint;
import tools.descartes.petstore.persistence.rest.OrderItemEndpoint;
import tools.descartes.petstore.persistence.rest.ProductEndpoint;
import tools.descartes.petstore.persistence.rest.UserEndpoint;
import tools.descartes.petstore.recommender.rest.RecommendEndpoint;
import tools.descartes.petstore.recommender.rest.RecommendSingleEndpoint;
import tools.descartes.petstore.recommender.rest.TrainEndpoint;
import tools.descartes.petstore.recommender.servlet.RecommenderStartup;
import tools.descartes.petstore.registry.rest.Registry;
import tools.descartes.petstore.registry.rest.RegistryREST;

/**
 * Abstract base for testing of the stores rest functionality.
 * 
 * @author Simon
 *
 */
public abstract class AbstractRecommenderRestTest {

	private static final String CONTEXT = "/test";
	private Tomcat registryTomcat;
	private Tomcat persistanceTomcat;
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
		// Setup registry
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

		// Setup persistance tomcat
		persistanceTomcat = new Tomcat();
		persistanceTomcat.setPort(3001);
		persistanceTomcat.setBaseDir(testWorkingDir);
		persistanceTomcat.enableNaming();
		Context context2 = persistanceTomcat.addWebapp("/tools.descartes.petstore.persistence", testWorkingDir);
		ContextEnvironment registryURL = new ContextEnvironment();
		registryURL.setDescription("");
		registryURL.setOverride(false);
		registryURL.setType("java.lang.String");
		registryURL.setName("registryURL");
		registryURL.setValue("http://localhost:3000/test/rest/services/");
		context2.getNamingResources().addEnvironment(registryURL);
		ContextEnvironment servicePort = new ContextEnvironment();
		servicePort.setDescription("");
		servicePort.setOverride(false);
		servicePort.setType("java.lang.String");
		servicePort.setName("servicePort");
		servicePort.setValue("3001");
		context2.getNamingResources().addEnvironment(servicePort);
		ResourceConfig restServletConfig2 = new ResourceConfig();
		restServletConfig2.register(CategoryEndpoint.class);
		restServletConfig2.register(OrderEndpoint.class);
		restServletConfig2.register(OrderItemEndpoint.class);
		restServletConfig2.register(ProductEndpoint.class);
		restServletConfig2.register(UserEndpoint.class);
		ServletContainer restServlet2 = new ServletContainer(restServletConfig2);
		persistanceTomcat.addServlet("/tools.descartes.petstore.persistence", "restServlet", restServlet2);
		context2.addServletMappingDecoded("/rest/*", "restServlet");
		context2.addApplicationListener(InitialDataGenerationDaemon.class.getName());
		persistanceTomcat.start();

		// Setup recommend tomcat
		testTomcat = new Tomcat();
		testTomcat.setPort(3002);
		testTomcat.setBaseDir(testWorkingDir);
		testTomcat.enableNaming();
		Context context3 = testTomcat.addWebapp("/tools.descartes.petstore.recommender", testWorkingDir);
		ContextEnvironment registryURL3 = new ContextEnvironment();
		registryURL3.setDescription("");
		registryURL3.setOverride(false);
		registryURL3.setType("java.lang.String");
		registryURL3.setName("registryURL");
		registryURL3.setValue("http://localhost:3000/test/rest/services/");
		context3.getNamingResources().addEnvironment(registryURL3);
		ContextEnvironment servicePort3 = new ContextEnvironment();
		servicePort3.setDescription("");
		servicePort3.setOverride(false);
		servicePort3.setType("java.lang.String");
		servicePort3.setName("servicePort");
		servicePort3.setValue("3002");
		context3.getNamingResources().addEnvironment(servicePort3);
		ResourceConfig restServletConfig3 = new ResourceConfig();
		restServletConfig3.register(TrainEndpoint.class);
		restServletConfig3.register(RecommendEndpoint.class);
		restServletConfig3.register(RecommendSingleEndpoint.class);
		ServletContainer restServlet3 = new ServletContainer(restServletConfig3);
		testTomcat.addServlet("/tools.descartes.petstore.recommender", "restServlet", restServlet3);
		context3.addServletMappingDecoded("/rest/*", "restServlet");
		context3.addApplicationListener(RecommenderStartup.class.getName());
		testTomcat.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

}
