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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.loadbalancer.Server;

import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerUpdaterDaemon;

/**
 * Client with common functionality for registering with the registry.
 *
 * @author Simon Eismann
 *
 */
public class RegistryClient {

  private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

  /**
   * The registry client.
   */
  private static RegistryClient client = new RegistryClient();
  private String registryRESTURL;
  private String hostName = null;
  private Integer port = null;

  private Server myServiceInstanceServer = null;
  private Service myService = null;

  private static final int LOAD_BALANCER_REFRESH_INTERVAL_MS = 2500;
  private static final int HEARTBEAT_INTERVAL_MS = 2500;

  private ScheduledExecutorService loadBalancerUpdateScheduler = Executors
      .newSingleThreadScheduledExecutor();
  private ScheduledExecutorService heartbeatScheduler = Executors
      .newSingleThreadScheduledExecutor();
  private ScheduledExecutorService availabilityScheduler = Executors
      .newSingleThreadScheduledExecutor();

  /**
   * Constructor.
   */
  protected RegistryClient() {
    System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
    String useHostIP = "false";
    try {
      useHostIP = (String) new InitialContext().lookup("java:comp/env/useHostIP");
    } catch (NamingException e) {
      LOG.warn("useHostIP not set. Not using host ip as hostname.");
    }
    try {
      if (useHostIP.equalsIgnoreCase("true")) {
        hostName = InetAddress.getLocalHost().getHostAddress();
      } else {
        hostName = (String) new InitialContext().lookup("java:comp/env/hostName");
      }
    } catch (NamingException e) {
      LOG.warn("hostName not set. Using default OS-provided hostname.");
    } catch (UnknownHostException e) {
      LOG.error("could not resolve host IP. Using default OS-provided hostname: " + e.getMessage());
    }
    try {
      port = Integer.parseInt((String) new InitialContext().lookup("java:comp/env/servicePort"));
    } catch (NamingException | NumberFormatException e) {
      LOG.warn("Could not read servicePort! Using port 8080 as fallback.");
      port = 8080;
    }
    try {
      registryRESTURL = (String) new InitialContext().lookup("java:comp/env/registryURL");
    } catch (NamingException e) {
      LOG.warn("registryURL not set. Falling back to default registry URL (localhost, port " + port
          + ").");
      registryRESTURL = "http://localhost:" + port
          + "/tools.descartes.teastore.registry/rest/services/";
    }
  }

  /**
   * Getter.
   *
   * @return registry client
   */
  public static RegistryClient getClient() {
    return client;
  }

  /**
   * Handles full registration.
   *
   * @param contextPath
   *          contextPath private String getContextPath(ServletContextEvent event)
   *          { return event.getServletContext().getContextPath(); }
   */
  public void unregister(String contextPath) {
    Service service = getService(contextPath);
    Server host = getServer();
    LOG.info("Shutting down " + service.getServiceName() + "@" + host);
    heartbeatScheduler.shutdownNow();
    loadBalancerUpdateScheduler.shutdownNow();
    availabilityScheduler.shutdownNow();
    try {
      loadBalancerUpdateScheduler.awaitTermination(20, TimeUnit.SECONDS);
      heartbeatScheduler.awaitTermination(20, TimeUnit.SECONDS);
      availabilityScheduler.awaitTermination(20, TimeUnit.SECONDS);
      RegistryClient.client.unregisterOnce(service, host);
    } catch (ProcessingException e) {
      LOG.warn("Could not unregister " + service.getServiceName() + " when it was shutting "
          + "down, since it could not reach the registry. This can be caused by shutting "
          + "down the registry before other services, but is in it self not a problem.");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles full unregistration.
   *
   * @param contextPath
   *          contextPath private String getContextPath(ServletContextEvent event)
   *          { return event.getServletContext().getContextPath(); }
   */
  public void register(String contextPath) {
    Service service = getService(contextPath);
    Server host = getServer();
    heartbeatScheduler.scheduleAtFixedRate(new RegistryClientHeartbeatDaemon(service, host), 0,
        HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    loadBalancerUpdateScheduler.scheduleAtFixedRate(new LoadBalancerUpdaterDaemon(), 1000,
        LOAD_BALANCER_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
  }

  /**
   * Calls the StartupCallback after the service is available.
   *
   * @param requestedService
   *          service to check for
   * @param myService
   *          The Service enum for the waiting service (the service calling this).
   * @param callback
   *          StartupCallback to call
   */
  public void runAfterServiceIsAvailable(Service requestedService, StartupCallback callback,
      Service myService) {
    availabilityScheduler.schedule(new StartupCallbackTask(requestedService, callback, myService),
        0, TimeUnit.NANOSECONDS);
    availabilityScheduler.shutdown();
  }

  /**
   * Get all servers for a service in the {@link Service} enum from the registry.
   *
   * @param targetService
   *          The service for which to get the servers.
   * @return List of servers.
   */
  public List<Server> getServersForService(Service targetService) {
    List<String> list = null;
    List<Server> serverList = new ArrayList<Server>();
    try {
      Response response = getRESTClient(5000).target(registryRESTURL)
          .path("/" + targetService.getServiceName() + "/").request(MediaType.APPLICATION_JSON)
          .get();
      list = response.readEntity(new GenericType<List<String>>() {
      });
    } catch (ProcessingException e) {
      return null;
    }

    if (list != null) {
      for (String string : list) {
        serverList.add(new Server(string));
      }
    }

    return serverList;
  }

  /**
   * Get the server for this service. Returns null if the service is not
   * registered yet.
   *
   * @return The server for this service. Null, if not registered.
   */
  public Server getMyServiceInstanceServer() {
    return myServiceInstanceServer;
  }

  /**
   * Get the service of this application. Returns null if the service is not
   * registered yet.
   *
   * @return The service for this application. Null, if not registered.
   */
  public Service getMyService() {
    return myService;
  }

  /**
   * Register a new server for a service in the registry.
   *
   * @param service
   *          The service for which to register.
   * @param server
   *          The server address.
   * @return True, if registration succeeded.
   */
  protected boolean registerOnce(Service service, Server server) {
    myService = service;
    myServiceInstanceServer = server;
    try {
      Response response = getRESTClient(5000).target(registryRESTURL).path(service.getServiceName())
          .path(server.toString()).request(MediaType.APPLICATION_JSON).put(Entity.text(""));
      return (response.getStatus() == Response.Status.OK.getStatusCode());
    } catch (ProcessingException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Unregister a server for a service in the registry.
   *
   * @param service
   *          The service for which to unregister.
   * @param server
   *          The server address to remove.
   * @return True, if unregistration succeeded.
   */
  private boolean unregisterOnce(Service service, Server server) {
    try {
      Response response = getRESTClient(1000).target(registryRESTURL).path(service.getServiceName())
          .path(server.toString()).request(MediaType.APPLICATION_JSON).delete();
      return (response.getStatus() == Response.Status.OK.getStatusCode());
    } catch (ProcessingException e) {
      return false;
    }
  }

  private Client getRESTClient(int timeout) {
    ClientConfig configuration = new ClientConfig();
    configuration.property(ClientProperties.CONNECT_TIMEOUT, timeout);
    configuration.property(ClientProperties.READ_TIMEOUT, timeout);
    return ClientBuilder.newClient(configuration);
  }

  private Service getService(String serviceName) {
    serviceName = cleanupServiceName(serviceName);
    for (Service service : Service.values()) {
      if (service.getServiceName().equals(serviceName)) {
        return service;
      }
    }
    throw new IllegalStateException(
        "The service " + serviceName + " is not registered in the Services enum");
  }

  private Server getServer() {
    return new Server(getHostName(), getPort());
  }

  private String getHostName() {
    if (hostName != null && !hostName.isEmpty()) {
      return hostName;
    }
    try {
      return InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException e) {
      throw new IllegalStateException("could not load hostname from OS.");
    }
  };

  private int getPort() {
    if (port != null) {
      return port;
    } else {
      throw new IllegalStateException("Could not read servicePort!");
    }
  }

  /**
   * Protected for testing.
   *
   * @param serviceName
   *          name of service
   * @return cleaned service name
   */
  protected String cleanupServiceName(String serviceName) {
    return serviceName.replace("/", "");
  }

  /**
   * Protected for test.
   *
   * @return scheduler
   */
  protected ScheduledExecutorService getHeartbeatScheduler() {
    return heartbeatScheduler;
  }

  /**
   * Protected for test.
   *
   * @return scheduler
   */
  protected ScheduledExecutorService getLoadBalancerUpdateScheduler() {
    return loadBalancerUpdateScheduler;
  }
}
