package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Arrivalrates
 */
@WebServlet("/Arrivalrates")
public class Arrivalrates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static Queue<Long> arrivals = new LinkedBlockingQueue<Long>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Arrivalrates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		for (Long arrival: arrivals)	
			response.getWriter().append(arrival + "\n");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
