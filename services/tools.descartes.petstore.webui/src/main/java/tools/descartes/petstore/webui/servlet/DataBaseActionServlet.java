package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.netflix.loadbalancer.Server;

import tools.descartes.petstore.registryclient.RegistryClient;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petstore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;

/**
 * Servlet implementation class DataBaseActionServlet
 */
@WebServlet("/dataBaseAction")
public class DataBaseActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	private static final String[] PARAMETERS = new String[] { "categories", "products", "users",
			"orders" };

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataBaseActionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.getParameter("confirm") != null) {

			String[] infos = extractOrderInformation(request);
			if (infos.length == 0) {
				// TODO
			} else {
				destroySessionBlob(getSessionBlob(request), response);
				Response resp = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE, "generatedb",
						String.class,
						client -> client.getService().path(client.getApplicationURI()).path(client.getEnpointURI())
								.queryParam(PARAMETERS[0], infos[0]).queryParam(PARAMETERS[1], infos[1])
								.queryParam(PARAMETERS[2], infos[2]).queryParam(PARAMETERS[3], infos[3])
								.request(MediaType.TEXT_PLAIN).get());
				if (resp.getStatus() == 200) {
					System.out.println("here");
				}
				
				// Regenerate images
				List<Integer> status = LoadBalancedImageOperations.regenerateImages();
				status.stream()
						.filter(r -> r != 200)
						.forEach(r -> System.out.println("An image provider service responded with " 
									+ r + " when regenerating images."));
//				// Retrain recommender
//				List<Response> recResp = ServiceLoadBalancer.multicastRESTOperation(Service.RECOMMENDER, "train",
//						null, client -> client.getService().path(client.getApplicationURI())
//						.path(client.getEnpointURI()).request(MediaType.TEXT_PLAIN).get());
//				recResp.stream()
//						.filter(r -> r.getStatus() != 200)
//						.forEach(r -> System.out.println("A recommender service responded with " 
//									+ r.getStatus() + " when retraining."));
			}

		}
		redirect("/", response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

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
