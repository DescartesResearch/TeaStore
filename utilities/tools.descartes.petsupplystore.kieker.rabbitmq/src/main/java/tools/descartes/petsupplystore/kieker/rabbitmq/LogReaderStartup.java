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
package tools.descartes.petsupplystore.kieker.rabbitmq;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 * @author Simon Eismann
 *
 */
@WebListener
public class LogReaderStartup implements ServletContextListener {
	private ScheduledExecutorService logReaderStarter = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Also set this accordingly in RegistryClientStartup.
	 */
	
	/**
	 * Empty constructor.
	 */
    public LogReaderStartup() {
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     * @param arg0 The servlet context event at destruction.
     */
    public void contextDestroyed(ServletContextEvent event)  { 
    	logReaderStarter.shutdownNow();
    	try {
			logReaderStarter.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     * @param arg0 The servlet context event at initialization.
     */
    public void contextInitialized(ServletContextEvent event)  {
    	logReaderStarter.schedule(new LogReaderDaemon(), 10, TimeUnit.SECONDS);
    }
    
}
