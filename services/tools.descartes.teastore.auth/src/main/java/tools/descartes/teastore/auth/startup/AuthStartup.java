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

package tools.descartes.teastore.auth.startup;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import io.opentracing.util.GlobalTracer;
import tools.descartes.teastore.registryclient.RegistryClient;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.tracing.Tracing;
import tools.descartes.teastore.registryclient.util.RESTClient;

/**
 * Application Lifecycle Listener implementation class Registry Client Startup.
 *
 * @author Simon Eismann
 *
 */
@WebListener
public class AuthStartup implements ServletContextListener {

  private static final int REST_READ_TIMOUT = 1750;

  /**
   * Also set this accordingly in RegistryClientStartup.
   */

  /**
   * Empty constructor.
   */
  public AuthStartup() {

  }

  /**
   * shutdown routine.
   * @see ServletContextListener#contextDestroyed(ServletContextEvent)
   * @param event The servlet context event at destruction.
   */
  public void contextDestroyed(ServletContextEvent event) {
    RegistryClient.getClient().unregister(event.getServletContext().getContextPath());
  }

  /**
   * startup routine.
   * @see ServletContextListener#contextInitialized(ServletContextEvent)
   * @param event The servlet context event at initialization.
   */
  public void contextInitialized(ServletContextEvent event) {
    GlobalTracer.register(Tracing.init(Service.AUTH.getServiceName()));
    RESTClient.setGlobalReadTimeout(REST_READ_TIMOUT);
    ServiceLoadBalancer.preInitializeServiceLoadBalancers(Service.PERSISTENCE);
    RegistryClient.getClient().register(event.getServletContext().getContextPath());
  }

}
