/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.descartes.teastore.webui.servlet;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Servlet for handling all cart actions.
 * 
 * @author Andre Bauer
 */
@WebServlet("/cartAction")
public class CartActionServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MM/yyyy");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartActionServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, LoadBalancerTimeoutException {
		for (Object paramo : request.getParameterMap().keySet()) {
			String param = (String) paramo;
			if (param.contains("addToCart")) {
				long productID = Long.parseLong(request.getParameter("productid"));
				SessionBlob blob = LoadBalancedStoreOperations.addProductToCart(getSessionBlob(request), productID);
				saveSessionBlob(blob, response);
				redirect("/cart", response, MESSAGECOOKIE, String.format(ADDPRODUCT, productID));
				break;
			} else if (param.contains("removeProduct")) {
				long productID = Long.parseLong(param.substring("removeProduct_".length()));
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
				break;
			}

		}

	}

	/**
	 * Handles the confirm order action. Saves the order into the sessionBlob
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String[] infos = extractOrderInformation(request);
		if (infos.length == 0) {
			redirect("/order", response);
		} else {

			SessionBlob blob = getSessionBlob(request);
			long price = 0;
			for (OrderItem item : blob.getOrderItems()) {
				price += item.getQuantity() * item.getUnitPriceInCents();
			}
			blob = LoadBalancedStoreOperations.placeOrder(getSessionBlob(request), infos[0] + " " + infos[1], infos[2],
					infos[3], infos[4],
					YearMonth.parse(infos[6], DTF).atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE), price, infos[5]);
			saveSessionBlob(blob, response);
			redirect("/", response, MESSAGECOOKIE, ORDERCONFIRMED);
		}

	}

	/**
	 * Extracts the user information from the input fields.
	 * 
	 * @param request
	 * @return String[] with user infos.
	 * 
	 */
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

	/**
	 * Updates the items in the cart.
	 * 
	 * @param request
	 * @param orderItems
	 * @param response
	 */
	private void updateOrder(HttpServletRequest request, List<OrderItem> orderItems, HttpServletResponse response) {
		SessionBlob blob = getSessionBlob(request);
		for (OrderItem orderItem : orderItems) {
			if (request.getParameter("orderitem_" + orderItem.getProductId()) != null) {
				blob = LoadBalancedStoreOperations.updateQuantity(blob, orderItem.getProductId(),
						Integer.parseInt(request.getParameter("orderitem_" + orderItem.getProductId())));
			}
		}
		saveSessionBlob(blob, response);
	}

}
