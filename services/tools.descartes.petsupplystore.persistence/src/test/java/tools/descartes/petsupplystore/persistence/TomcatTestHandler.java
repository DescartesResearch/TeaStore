package tools.descartes.petsupplystore.persistence;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import tools.descartes.petsupplystore.persistence.domain.CategoryRepository;
import tools.descartes.petsupplystore.persistence.repository.EMFManagerInitializer;
import tools.descartes.petsupplystore.persistence.rest.CategoryEndpoint;

/**
 * Creates and manages an embedded Tomcat for testing.
 * @author Jóakim von Kistowski
 *
 */
public class TomcatTestHandler {

	/**
	 * Default context for testing webapp.
	 */
	public static final String CONTEXT = "/test";
	
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	private Tomcat[] tomcats;
	
	/**
	 * Create a Tomcat test handler for persistence testing.
	 * @param count Number of testing tomcats.
	 * @param startPort Port to start with (use 0 for auto-assigning).
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 */
	public TomcatTestHandler(int count, int startPort, Class<?>... endpoints) throws ServletException, LifecycleException {
		tomcats = new Tomcat[count];
		EMFManagerInitializer.initializeEMF();
		for (int i = 0; i < count; i++) {
			tomcats[i] = new Tomcat();
			tomcats[i].setPort(startPort + i);
			tomcats[i].setBaseDir(testWorkingDir);
			Context context = tomcats[i].addWebapp(CONTEXT, testWorkingDir);
			ResourceConfig restServletConfig = new ResourceConfig();
			for (Class<?> endpoint: endpoints) {
				restServletConfig.register(endpoint);
			}
			ServletContainer restServlet = new ServletContainer(restServletConfig);
			tomcats[i].addServlet(CONTEXT, "restServlet", restServlet);
			context.addServletMappingDecoded("/rest/*", "restServlet");
			tomcats[i].start();
		}
		System.out.println("Initializing Database with size " + CategoryRepository.REPOSITORY.getAllEntities().size());
	}
	
	/**
	 * Create a Tomcat test handler.
	 * @param endpoints Class objects for the endpoints.
	 * @throws ServletException Exception on failure.
	 * @throws LifecycleException Exception on failure.
	 */
	public TomcatTestHandler(Class<?>... endpoints) throws ServletException, LifecycleException {
		this(1, 0, endpoints);
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
			tomcats[index].destroy();
			if (tomcats[index].getServer() != null
					&& tomcats[index].getServer().getState() != LifecycleState.DESTROYED) {
		        if (tomcats[index].getServer().getState() != LifecycleState.STOPPED) {
		        	tomcats[index].stop();
		        }
		    }
		} catch (LifecycleException e) {
			System.out.println("Exception shutting down Testing Tomcat (this may happen a lot): " + e.getMessage());
		}
	}
}
