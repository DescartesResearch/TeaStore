package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class Cart
 */
@WebServlet("/cart")
public class CartServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartServlet() {
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
		SessionBlob blob = getSessionBlob(request);

		List<OrderItem> orderItems = blob.getOrderItems();
		ArrayList<Long> ids = new ArrayList<Long>();
		for (OrderItem orderItem : orderItems) {
			ids.add(orderItem.getProductId());
		}

		HashMap<Long, Product> products = new HashMap<Long, Product>();
		for (Long id : ids) {
			Product product = LoadBalancedStoreOperations.getProduct(id);
			products.put(product.getId(), product);
		}

		request.setAttribute("storeIcon", LoadBalancedImageOperations.getWebImage("icon", ImageSize.ICON));
		request.setAttribute("title", "Pet Supply Store Cart");
		request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
		request.setAttribute("OrderItems", orderItems);
		request.setAttribute("Products", products);
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));
		
		List<Product> ad = LoadBalancedStoreOperations.getAdvertisements(blob, 3);
		request.setAttribute("Advertisment", ad);
		
		request.setAttribute("productImages", LoadBalancedImageOperations.getProductPreviewImages(ad));

		request.getRequestDispatcher("WEB-INF/pages/cart.jsp").forward(request, response);

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
