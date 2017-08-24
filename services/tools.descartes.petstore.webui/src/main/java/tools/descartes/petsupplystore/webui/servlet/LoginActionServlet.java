package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class LoginActionServlet
 */
@WebServlet("/loginAction")
public class LoginActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginActionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		redirect("/", response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean login = false;
		boolean redirect = false;
		if (request.getParameter("username") != null && request.getParameter("password") != null) {
			SessionBlob blob = LoadBalancedStoreOperations.login(getSessionBlob(request),
					request.getParameter("username"), request.getParameter("password"));
			saveSessionBlob(blob, response);
			login = (blob.getSID() != null);

			if (login) {
				if (request.getParameter("referer") != null
						&& request.getParameter("referer").contains("tools.descartes.petsupplystore.webui/cart")) {
					redirect = redirect("/cart", response, MESSAGECOOKIE, SUCESSLOGIN);
				} else {
					redirect = redirect("/", response, MESSAGECOOKIE, SUCESSLOGIN);
				}

			}

		} else if (request.getParameter("logout") != null) {
			SessionBlob blob = LoadBalancedStoreOperations.logout(getSessionBlob(request));
			saveSessionBlob(blob, response);
			destroySessionBlob(blob, response);
			redirect = redirect("/", response, MESSAGECOOKIE, SUCESSLOGOUT);

		}
		if (!redirect) {

			doGet(request, response);
		}

	}

}
