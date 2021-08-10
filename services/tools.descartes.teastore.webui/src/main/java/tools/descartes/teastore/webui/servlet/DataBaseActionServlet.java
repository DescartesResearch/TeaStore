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
import jakarta.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.rest.LoadBalancedImageOperations;

/**
 * Servlet implementation for handling the data base action.
 * @author Andre
 */
@WebServlet("/dataBaseAction")
public class DataBaseActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	private static final String[] PARAMETERS = new String[] { "categories", "products", "users", "orders" };
	private static final Logger LOG = LoggerFactory.getLogger(DataBaseActionServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataBaseActionServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, LoadBalancerTimeoutException {

		if (request.getParameter("confirm") != null) {

			String[] infos = extractOrderInformation(request);
			if (infos.length == 0) {
				redirect("/database", response);
			} else {
				destroySessionBlob(getSessionBlob(request), response);
				Response resp = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "generatedb",
						String.class,
						client -> client.getService().path(client.getApplicationURI()).path(client.getEndpointURI())
								.queryParam(PARAMETERS[0], infos[0]).queryParam(PARAMETERS[1], infos[1])
								.queryParam(PARAMETERS[2], infos[2]).queryParam(PARAMETERS[3], infos[3])
								.request(MediaType.TEXT_PLAIN).get());
				//buffer entity to release connections
				resp.bufferEntity();
				if (resp.getStatus() == 200) {
					LOG.info("DB is re-generating.");
				}

				// Regenerate images
				List<Integer> status = LoadBalancedImageOperations.regenerateImages();
				status.stream().filter(r -> r != 200).forEach(
						r -> LOG.warn("An image provider service responded with " + r + " when regenerating images."));
				// Retrain recommender
				List<Response> recResp = ServiceLoadBalancer.multicastRESTOperation(Service.RECOMMENDER, "train",
						String.class,
						client -> client.getEndpointTarget().path("async").request(MediaType.TEXT_PLAIN).get());
				recResp.stream().filter(r -> r.getStatus() != 200).forEach(
						r -> LOG.warn("A recommender service responded with " + r.getStatus() + " when retraining."));
				//buffer entity to release connections
				recResp.stream().forEach(r -> r.bufferEntity());
				redirect("/status", response);
			}

		} else {
			redirect("/", response);
		}
	}

	/**
	 * Extracts the information from the input fields.
	 * 
	 * @param request
	 * @return String[] with the info for the database generation
	 */
	private String[] extractOrderInformation(HttpServletRequest request) {

		String[] infos = new String[PARAMETERS.length];
		for (int i = 0; i < PARAMETERS.length; i++) {
			if (request.getParameter(PARAMETERS[i]) == null) {
				return new String[0];
			} else {
				infos[i] = request.getParameter(PARAMETERS[i]);
			}
		}
		return infos;
	}

}
