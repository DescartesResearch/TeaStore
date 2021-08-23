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
package tools.descartes.teastore.kieker.rabbitmq;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.BasicConfigurator;

/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 * 
 * @author Simon Eismann
 *
 */
@WebListener
public class LogReaderStartup implements ServletContextListener {
  private static ScheduledExecutorService logReaderStarter;
  private static ScheduledExecutorService fileWriterStarter;

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
   * @param event
   *          The servlet context event at destruction.
   */
  public void contextDestroyed(ServletContextEvent event) {
    stopFileWriter();
    logReaderStarter.shutdownNow();
    try {
      logReaderStarter.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * stops the filewriter.
   */
  public static void stopFileWriter() {
    fileWriterStarter.shutdownNow();
    try {
      fileWriterStarter.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * @see ServletContextListener#contextInitialized(ServletContextEvent)
   * @param event
   *          The servlet context event at initialization.
   */
  public void contextInitialized(ServletContextEvent event) {
    startFileWriter();
    logReaderStarter = Executors.newSingleThreadScheduledExecutor();
    BasicConfigurator.configure();
    logReaderStarter.schedule(new LogReaderDaemon(), 10, TimeUnit.SECONDS);
  }

  /**
   * Starts the filewriter.
   */
  public static void startFileWriter() {
    fileWriterStarter = Executors.newSingleThreadScheduledExecutor();
    fileWriterStarter.schedule(new FileWriterDaemon(), 10, TimeUnit.SECONDS);
  }

}
