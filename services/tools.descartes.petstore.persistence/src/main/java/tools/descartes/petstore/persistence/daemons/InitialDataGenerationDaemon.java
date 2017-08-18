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
package tools.descartes.petstore.persistence.daemons;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import tools.descartes.petstore.persistence.repository.DataGenerator;

/**
 * Application Lifecycle Listener implementation class for data generation.
 * @author Joakim von Kistowski
 *
 */
@WebListener
public class InitialDataGenerationDaemon implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public InitialDataGenerationDaemon() {
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     * @param arg0 The servlet context event at destruction.
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     * @param arg0 The servlet context event at initialization.
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	if (DataGenerator.GENERATOR.isDatabaseEmpty()) {
    		DataGenerator.GENERATOR.generateDatabaseContent(DataGenerator.SMALL_DB_CATEGORIES,
    				DataGenerator.SMALL_DB_PRODUCTS_PER_CATEGORY, DataGenerator.SMALL_DB_USERS,
    				DataGenerator.SMALL_DB_MAX_ORDERS_PER_USER);
    	}
    }
}
