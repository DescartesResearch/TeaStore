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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.descartes.petsupplystore.entities.ImageSizePreset;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.petsupplystore.webui.servlet.elhelper.ELHelperUtils;

/**
 * Servlet implementation for the web view of "Product"
 * 
 * @author Andre Bauer
 */
@WebServlet("/product")
public class ProductServlet extends AbstractUIServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ProductServlet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGetInternal(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, LoadBalancerTimeoutException {
		checkforCookie(request, response);
		if (request.getParameter("id") != null) {
			long id = Long.valueOf(request.getParameter("id"));
			request.setAttribute("CategoryList", LoadBalancedStoreOperations.getCategories());
			request.setAttribute("title", "Tea Store Product");
			SessionBlob blob = getSessionBlob(request);
			request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(blob));
			Product p = LoadBalancedStoreOperations.getProduct(id);
			request.setAttribute("product", p);

			List<Product> ad = LoadBalancedStoreOperations.getAdvertisements(blob, p.getId());
			if (ad.size() > 3) {
				ad.subList(3, ad.size()).clear();
			}
			request.setAttribute("Advertisment", ad);

			request.setAttribute("productImages", LoadBalancedImageOperations.getProductImages(ad, 
					ImageSizePreset.RECOMMENDATION.getSize()));
			request.setAttribute("productImage", LoadBalancedImageOperations.getProductImage(p));
			request.setAttribute("storeIcon", 
					LoadBalancedImageOperations.getWebImage("icon", ImageSizePreset.ICON.getSize()));
			request.setAttribute("helper", ELHelperUtils.UTILS);

			request.getRequestDispatcher("WEB-INF/pages/product.jsp").forward(request, response);
		} else {
			redirect("/", response);
		}
	}

}
