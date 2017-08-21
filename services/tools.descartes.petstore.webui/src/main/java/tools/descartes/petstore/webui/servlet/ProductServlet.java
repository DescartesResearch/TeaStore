package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class ProductServlet
 */
@WebServlet("/product")
public class ProductServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProductServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		checkforCookie(request,response);
		if (request.getParameter("id") != null) {
			long id = Long.valueOf(request.getParameter("id"));
			request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
			request.setAttribute("title", "Pet Supply Store Product");
			SessionBlob blob = getSessionBlob(request);
			request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(blob));
			Product p = LoadBalancedStoreOperations.getProduct(id);
			request.setAttribute("product", p);

			List<Product> ad = LoadBalancedStoreOperations.getAdvertisements(blob, p.getId());
			if (ad.size() > 3) {
				ad.subList(3, ad.size()).clear();
			}
			request.setAttribute("Advertisment", ad);
			
			request.setAttribute("productImages", LoadBalancedImageOperations.getProductPreviewImages(ad));
			request.setAttribute("productImage", LoadBalancedImageOperations.getProductImage(p));
			request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
			
			request.getRequestDispatcher("WEB-INF/pages/product.jsp").forward(request, response);
		} else {
			redirect("/", response);
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
