package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;

/**
 * Servlet implementation class DataBaseActionServlet
 */
@WebServlet("/dataBaseAction")
public class DataBaseActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	private static final String[] parameters = new String[] { "categories", "products", "users",
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
								.queryParam(parameters[0], infos[0]).queryParam(parameters[1], infos[1])
								.queryParam(parameters[2], infos[2]).queryParam(parameters[3], infos[3])
								.request(MediaType.TEXT_PLAIN).get());
				if (resp.getStatus() == 200) {
					System.out.println("here");
				}

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

		String[] infos = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			if (request.getParameter(parameters[i]) == null) {
				return new String[0];
			} else {
				infos[i] = request.getParameter(parameters[i]);
			}
		}
		return infos;
	}

}
