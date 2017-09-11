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

package tools.descartes.petsupplystore.webui.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.client.ClientException;

import tools.descartes.petsupplystore.entities.ImageSizePreset;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Servlet implementation for the web view of "Cart"
 * 
 * @author Andre Bauer
 */
@WebServlet("/cart")
public class CartServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGetInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ClientException {
		checkforCookie(request, response);
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

		request.setAttribute("storeIcon", 
				LoadBalancedImageOperations.getWebImage("icon", ImageSizePreset.ICON.getSize()));
		request.setAttribute("title", "Pet Supply Store Cart");
		request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
		request.setAttribute("OrderItems", orderItems);
		request.setAttribute("Products", products);
		request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(getSessionBlob(request)));

		List<Product> ad = LoadBalancedStoreOperations.getAdvertisements(blob);
		if (ad.size() > 3) {
			ad.subList(3, ad.size()).clear();
		}
		request.setAttribute("Advertisment", ad);

		request.setAttribute("productImages", LoadBalancedImageOperations.getProductPreviewImages(ad));

		request.getRequestDispatcher("WEB-INF/pages/cart.jsp").forward(request, response);

	}

}
