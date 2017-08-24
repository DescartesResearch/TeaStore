package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class ProceedServelet
 */
@WebServlet("/order")
public class OrderServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderServlet() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		checkforCookie(request,response);
		if (getSessionBlob(request).getOrderItems().size() == 0) {
			redirect("/", response);
		} else {
			doPost(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("title", "Pet Supply Store Order");
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
		request.getRequestDispatcher("WEB-INF/pages/order.jsp").forward(request, response);
	}

}
