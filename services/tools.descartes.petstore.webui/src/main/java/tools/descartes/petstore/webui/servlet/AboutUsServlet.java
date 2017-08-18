package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class AboutUsServlet
 */
@WebServlet("/about")
public class AboutUsServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AboutUsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkforCookie(request,response);
		HashMap<String, String> portraits = LoadBalancedImageOperations.getWebImages(
				Arrays.asList("andreBauer", "johannesGrohmann", "joakimKistowski", "simonEismann", "norbertSchmitt"), 
				ImageSize.PORTRAIT);
		request.setAttribute("portraitAndre", portraits.get("andreBauer"));
		request.setAttribute("portraitJohannes", portraits.get("johannesGrohmann"));
		request.setAttribute("portraitJoakim", portraits.get("joakimKistowski"));	
		request.setAttribute("portraitSimon", portraits.get("simonEismann"));	
		request.setAttribute("portraitNorbert", portraits.get("norbertSchmitt"));
		request.setAttribute("descartesLogo", LoadBalancedImageOperations.getWebImage("descartesLogo", ImageSize.LOGO));
		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("title", "Pet Supply Store About Us");
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
		
		request.getRequestDispatcher("WEB-INF/pages/about.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
