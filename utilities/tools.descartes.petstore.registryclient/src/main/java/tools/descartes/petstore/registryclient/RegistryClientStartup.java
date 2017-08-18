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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.netflix.loadbalancer.Server;

import tools.descartes.petstore.registryclient.loadbalancers.LoadBalancerUpdaterDaemon;

/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 * @author Simon Eismann
 *
 */
@WebListener
public class RegistryClientStartup implements ServletContextListener {
	
	private static final int LOAD_BALANCER_REFRESH_INTERVAL_MS = 2500;
	/**
	 * Set this accordingly in RegistryStartup.
	 */
	private static final int HEARTBEAT_INTERVAL_MS = 2500;
	
	private ScheduledExecutorService loadBalancerUpdateScheduler;
	private ScheduledExecutorService heartbeatScheduler;
    /**
     * Default constructor. 
     */
    public RegistryClientStartup() {
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     * @param arg0 The servlet context event at destruction.
     */
    public void contextDestroyed(ServletContextEvent arg0)  {
    	Service service = getService(getContextPath(arg0));
    	Server host = getServer();
    	System.out.println("Shutdown " + service.getServiceName() + "@" + host);
    	RegistryClient.CLIENT.unregister(service, host);
    	heartbeatScheduler.shutdown();
    	loadBalancerUpdateScheduler.shutdown();
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     * @param arg0 The servlet context event at initialization.
     */
    public void contextInitialized(ServletContextEvent arg0)  {
    	Service service = getService(getContextPath(arg0));
    	Server host = getServer();
		heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
		heartbeatScheduler.scheduleAtFixedRate(
				new RegistryClientHeartbeatDaemon(service, host), 0,
				HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
		loadBalancerUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
		loadBalancerUpdateScheduler.scheduleAtFixedRate(new LoadBalancerUpdaterDaemon(), 1000,
				LOAD_BALANCER_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    private Service getService(String serviceName) {
    	serviceName = cleanupServiceName(serviceName);
    	for (Service service : Service.values()) {
    		if (service.getServiceName().equals(serviceName)) {
    			return service;
    		}
    	}
    	throw new IllegalStateException("The service " + serviceName + " is not registered in the Services enum");
    }
    
    /**
     * Cleanup the service name. Removes unecessary "/" characters.
     * @param serviceName The name to clean.
     * @return The cleaned name.
     */
    protected String cleanupServiceName(String serviceName) {
    	return serviceName.replace("/", "");
    }
    
    private Server getServer() {
    	return new Server(getHostName(), Integer.valueOf(getPort()));
    }
    
    private String getHostName() {
    	try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			throw new IllegalStateException("could not load hostname");
		}
    };
    
    private String getContextPath(ServletContextEvent event) {
    	return event.getServletContext().getContextPath();
    }
    
    private String getPort() {
		try {
			return (String) new InitialContext().lookup("java:comp/env/servicePort");
		} catch (NamingException e) {
			throw new IllegalStateException("Could not read servicePort!");
		}
    }
    
    /**
     * Get the registration scheduler.
     * @return The registration scheduler.
     */
    protected ScheduledExecutorService getHeartbeatScheduler() {
    	return heartbeatScheduler;
    }
    
    /**
     * Get the load balancer update scheduler.
     * @return The load balancer update scheduler.
     */
    protected ScheduledExecutorService getLoadBalancerUpdateScheduler() {
    	return loadBalancerUpdateScheduler;
    }
}
