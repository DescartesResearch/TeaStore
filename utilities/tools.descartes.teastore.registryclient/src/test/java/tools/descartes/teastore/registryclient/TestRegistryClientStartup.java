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
package tools.descartes.teastore.registryclient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 * @author Simon Eismann
 *
 */
public class TestRegistryClientStartup implements ServletContextListener {
	
	/**
	 * Also set this accordingly in RegistryClientStartup.
	 */
	
	/**
	 * Empty constructor.
	 */
    public TestRegistryClientStartup() {
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     * @param event The servlet context event at destruction.
     */
    public void contextDestroyed(ServletContextEvent event)  { 
    	TestRegistryClient.getClient().getHeartbeatScheduler().shutdown();
    	TestRegistryClient.getClient().getLoadBalancerUpdateScheduler().shutdown();
    	TestRegistryClient.getClient().unregister(event.getServletContext().getContextPath());
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     * @param event The servlet context event at initialization.
     */
    public void contextInitialized(ServletContextEvent event)  {
    	TestRegistryClient.getClient().register(event.getServletContext().getContextPath());
    }
    
}
