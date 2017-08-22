package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;

/**
 * Servlet to show database status.
 * @author Joakim von Kistowski
 */
@WebServlet("/databasestatus")
public class DataBaseStatusServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataBaseStatusServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkforCookie(request,response);
		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("title", "Pet Supply Store Status");
		
		//get database finished state
		String finished = ServiceLoadBalancer.loadBalanceRESTOperation(
				Service.PERSISTENCE, "generatedb", String.class,
				client -> client.getEndpointTarget().path("finished").request(MediaType.TEXT_PLAIN)
				.get().readEntity(String.class));
		boolean dbfinished = false;
		if (finished != null) {
			dbfinished = Boolean.parseBoolean(finished);
		}
		request.setAttribute("dbfinished", dbfinished);
		request.getRequestDispatcher("WEB-INF/pages/databasestatus.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
