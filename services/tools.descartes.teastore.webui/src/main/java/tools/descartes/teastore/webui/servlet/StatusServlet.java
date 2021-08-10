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

package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;

import tools.descartes.teastore.registryclient.RegistryClient;
import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.teastore.entities.ImageSizePreset;

/**
 * Servlet to show database and other service status.
 *
 * @author Joakim von Kistowski
 */
@WebServlet("/status")
public class StatusServlet extends AbstractUIServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public StatusServlet() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, LoadBalancerTimeoutException {
    checkforCookie(request, response);
    String iconImage = null;
    try {
      iconImage = LoadBalancedImageOperations.getWebImage("icon", ImageSizePreset.ICON.getSize());
    } catch (NullPointerException e) {

    }
    request.setAttribute("storeIcon", iconImage);
    request.setAttribute("title", "TeaStore Status");

    boolean noregistry = false;
    try {
      request.setAttribute("webuiservers",
          RegistryClient.getClient().getServersForService(Service.WEBUI));
      request.setAttribute("authenticationservers",
          RegistryClient.getClient().getServersForService(Service.AUTH));
      request.setAttribute("persistenceservers",
          RegistryClient.getClient().getServersForService(Service.PERSISTENCE));
      request.setAttribute("imageservers",
          RegistryClient.getClient().getServersForService(Service.IMAGE));
      request.setAttribute("recommenderservers",
          RegistryClient.getClient().getServersForService(Service.RECOMMENDER));
      request.setAttribute("dbfinished", isDatabaseFinished());
      request.setAttribute("imagefinished", isImageFinished());
      request.setAttribute("recommenderfinished", isRecommenderFinished());
    } catch (NullPointerException e) {
      noregistry = true;
    }
    request.setAttribute("noregistry", noregistry);

    request.getRequestDispatcher("WEB-INF/pages/status.jsp").forward(request, response);
  }

  /**
   * Checks if Database is created.
   *
   * @return status
   */
  private boolean isDatabaseFinished() {
    String finished = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE,
        "generatedb", String.class, client -> client.getEndpointTarget().path("finished")
            .request(MediaType.TEXT_PLAIN).get().readEntity(String.class));
    if (finished != null) {
      return Boolean.parseBoolean(finished);
    }
    return false;
  }

  /**
   * Checks if ImageProvider is ready.
   *
   * @return status
   */
  private boolean isImageFinished() {
    List<String> finishedMessages = ServiceLoadBalancer.multicastRESTOperation(Service.IMAGE,
        "image", String.class, client -> client.getEndpointTarget().path("finished")
            .request(MediaType.APPLICATION_JSON).get().readEntity(String.class));
    if (finishedMessages != null && !finishedMessages.isEmpty()) {
      boolean finished = true;
      for (String finishedMessage : finishedMessages) {
        finished = finished && Boolean.parseBoolean(finishedMessage);
      }
      return finished;
    }
    return false;
  }

  /**
   * Checks if Recommender has finished training.
   *
   * @return status
   */
  private boolean isRecommenderFinished() {
    List<String> finishedMessages = ServiceLoadBalancer.multicastRESTOperation(Service.RECOMMENDER,
        "train", String.class, client -> client.getEndpointTarget().path("isready")
            .request(MediaType.TEXT_PLAIN).get().readEntity(String.class));
    if (finishedMessages != null && !finishedMessages.isEmpty()) {
      return finishedMessages.stream().map(b -> Boolean.parseBoolean(b)).reduce(Boolean.TRUE,
          (a, b) -> a && b);
    }
    return false;
  }

}
