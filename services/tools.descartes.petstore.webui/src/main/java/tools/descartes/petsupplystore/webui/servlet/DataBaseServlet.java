package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;

/**
 * Servlet implementation class DataBaseServlet
 */
@WebServlet("/database")
public class DataBaseServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataBaseServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkforCookie(request,response);
		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("title", "Pet Supply Store Database");
		request.getRequestDispatcher("WEB-INF/pages/database.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
