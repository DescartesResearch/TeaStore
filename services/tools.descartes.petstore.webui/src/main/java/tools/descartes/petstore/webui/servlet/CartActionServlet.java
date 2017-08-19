package tools.descartes.petstore.webui.servlet;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation class UpdateCartServlet
 */
@WebServlet("/cartAction")
public class CartActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yyyy");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartActionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		for (Object paramo : request.getParameterMap().keySet()) {
			String param = (String) paramo;
			if (param.contains("addToCart")) {
				long productID = Long.valueOf(request.getParameter("productid"));
				SessionBlob blob = LoadBalancedStoreOperations.addProductToCart(getSessionBlob(request), productID);
				saveSessionBlob(blob, response);
				redirect("/cart", response, MESSAGECOOKIE, String.format(ADDPRODUCT, productID));
				break;
			} else if (param.contains("removeProduct")) {
				long productID = Long.valueOf(param.substring("removeProduct_".length()));
				SessionBlob blob = LoadBalancedStoreOperations.removeProductFromCart(getSessionBlob(request),
						productID);
				saveSessionBlob(blob, response);
				redirect("/cart", response, MESSAGECOOKIE, String.format(REMOVEPRODUCT, productID));
				break;
			} else if (param.contains("updateCartQuantities")) {
				List<OrderItem> orderItems = getSessionBlob(request).getOrderItems();
				updateOrder(request, orderItems, response);
				redirect("/cart", response, MESSAGECOOKIE, CARTUPDATED);
				break;
			} else if (param.contains("proceedtoCheckout")) {
				if (LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request))) {
					List<OrderItem> orderItems = getSessionBlob(request).getOrderItems();
					updateOrder(request, orderItems, response);
					redirect("/order", response);
				} else {
					redirect("/login", response);
				}
				break;
			} else if (param.contains("confirm")) {
				confirmOrder(request, response);
				redirect("/", response, MESSAGECOOKIE, ORDERCONFIRMED);
				break;
			}

		}

	}

	private void confirmOrder(HttpServletRequest request, HttpServletResponse response) {

		String[] infos = extractOrderInformation(request);
		if (infos.length == 0) {
			// TODO
		} else {

			SessionBlob blob = getSessionBlob(request);
			long price = 0;
			for (OrderItem item : blob.getOrderItems()) {
				price += item.getQuantity() * item.getUnitPriceInCents();
			}
			blob = LoadBalancedStoreOperations.placeOrder(getSessionBlob(request), infos[0] + " " + infos[1],
					infos[2], infos[3], infos[4], 
					YearMonth.parse(infos[6], dtf).atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE), price, infos[5]);
			saveSessionBlob(blob, response);
		}

	}

	private String[] extractOrderInformation(HttpServletRequest request) {

		String[] parameters = new String[] { "firstname", "lastname", "address1", "address2", "cardtype", "cardnumber",
				"expirydate" };
		String[] infos = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			if (request.getParameter(parameters[i]) == null) {
				return new String[0];
			} else {
				infos[i] = request.getParameter(parameters[i]);
			}
		}
		return infos;
	}

	private void updateOrder(HttpServletRequest request, List<OrderItem> orderItems, HttpServletResponse response) {
		SessionBlob blob = getSessionBlob(request);
		for (OrderItem orderItem : orderItems) {
			if (request.getParameter("orderitem_" + orderItem.getProductId()) != null) {
				blob = LoadBalancedStoreOperations.updateQuantity(blob, orderItem.getProductId(),
						Integer.valueOf(request.getParameter("orderitem_" + orderItem.getProductId())));
			}
		}
		saveSessionBlob(blob, response);
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
