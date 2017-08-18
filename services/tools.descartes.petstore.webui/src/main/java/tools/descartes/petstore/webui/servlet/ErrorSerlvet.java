package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class ErrorSerlvet
 */
@WebServlet("/error")
public class ErrorSerlvet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorSerlvet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

		if(statusCode == null) {
			redirect("/", response);
		} else {
			request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
			request.setAttribute("errorImage", LoadBalancedImageOperations.getWebImage("error", ImageSize.MAIN_IMAGE));
			request.setAttribute("title", "Pet Supply Store Error ");
			request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
			request.getRequestDispatcher("WEB-INF/pages/error.jsp").forward(request, response);

		}
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

}
