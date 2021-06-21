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
import java.util.LinkedList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedImageOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedRecommenderOperations;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.webui.servlet.elhelper.ELHelperUtils;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.ImageSizePreset;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Servlet implementation for the web view of "Product".
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
  protected void handleGETRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, LoadBalancerTimeoutException {
    checkforCookie(request, response);
    if (request.getParameter("id") != null) {
      Long id = Long.valueOf(request.getParameter("id"));
      request.setAttribute("CategoryList", LoadBalancedCRUDOperations
          .getEntities(Service.PERSISTENCE, "categories", Category.class, -1, -1));
      request.setAttribute("title", "TeaStore Product");
      SessionBlob blob = getSessionBlob(request);
      request.setAttribute("login", LoadBalancedStoreOperations.isLoggedIn(blob));
      Product p = LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "products",
          Product.class, id);
      request.setAttribute("product", p);

      List<OrderItem> items = new LinkedList<>();
      OrderItem oi = new OrderItem();
      oi.setProductId(id);
      oi.setQuantity(1);
      items.add(oi);
      items.addAll(getSessionBlob(request).getOrderItems());
      List<Long> productIds = LoadBalancedRecommenderOperations.getRecommendations(items,
          getSessionBlob(request).getUID());
      List<Product> ads = new LinkedList<Product>();
      for (Long productId : productIds) {
        ads.add(LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "products", Product.class,
            productId));
      }

      if (ads.size() > 3) {
        ads.subList(3, ads.size()).clear();
      }
      request.setAttribute("Advertisment", ads);

      request.setAttribute("productImages", LoadBalancedImageOperations.getProductImages(ads,
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
