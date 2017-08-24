package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.registryclient.RegistryClient;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;

/**
 * Servlet to show database and other service status.
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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkforCookie(request,response);
		String iconImage = null;
		try {
			iconImage = LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON);
		} catch (NullPointerException e) {
			
		}
		request.setAttribute("storeIcon", iconImage);
		request.setAttribute("title", "Pet Supply Store Status");
		
		boolean noregistry = false;
		try {
			request.setAttribute("webuiservers", RegistryClient.getClient().getServersForService(Service.WEBUI));
			request.setAttribute("storeservers", RegistryClient.getClient().getServersForService(Service.STORE));
			request.setAttribute("persistenceservers", RegistryClient.getClient().getServersForService(Service.PERSISTENCE));
			request.setAttribute("imageservers", RegistryClient.getClient().getServersForService(Service.IMAGE));
			request.setAttribute("recommenderservers", RegistryClient.getClient().getServersForService(Service.RECOMMENDER));
			request.setAttribute("dbfinished", isDatabaseFinished());
			request.setAttribute("imagefinished", isImageFinished());
		} catch (NullPointerException e) {
			noregistry = true;
		}
		request.setAttribute("noregistry", noregistry);
		
		request.getRequestDispatcher("WEB-INF/pages/status.jsp").forward(request, response);
	}
	
	private boolean isDatabaseFinished() {
		String finished = ServiceLoadBalancer.loadBalanceRESTOperation(
				Service.PERSISTENCE, "generatedb", String.class,
				client -> client.getEndpointTarget().path("finished").request(MediaType.TEXT_PLAIN)
				.get().readEntity(String.class));
		if (finished != null) {
			return Boolean.parseBoolean(finished);
		}
		return false;
	}
	
	private boolean isImageFinished() {
		List<String> finishedMessages = ServiceLoadBalancer.multicastRESTOperation(
				Service.IMAGE, "image", String.class,
				client -> client.getEndpointTarget().path("finished").request(MediaType.APPLICATION_JSON)
				.get().readEntity(String.class));
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
