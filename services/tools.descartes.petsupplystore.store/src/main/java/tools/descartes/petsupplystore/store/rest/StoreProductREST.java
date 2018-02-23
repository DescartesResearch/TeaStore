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
package tools.descartes.petsupplystore.store.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedRecommenderOperations;
import tools.descartes.petsupplystore.rest.NotFoundException;
import tools.descartes.petsupplystore.rest.TimeoutException;

/**
 * Rest endpoint for the store product.
 * 
 * @author Simon
 */
@Path("products")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class StoreProductREST {

	/**
	 * Get advertisement.
	 * 
	 * @param order
	 *            current cart state
	 * @param pid
	 *            pid
	 * @return Response containing product
	 */
	@POST
	@Path("ads")
	public Response getAdvertisement(List<OrderItem> orderItems, @QueryParam("pid") final Long pid, @QueryParam("uid") final Long uid) {
		List<OrderItem> items = new LinkedList<>();
		if (pid != null) {
			OrderItem oi = new OrderItem();
			oi.setProductId(pid);
			oi.setQuantity(1);
			items.add(oi);
		}
		items.addAll(orderItems);
		List<Long> productIds;
		try {
			productIds = LoadBalancedRecommenderOperations.getRecommendations(items, uid);
		} catch (TimeoutException e) {
			return Response.status(408).build();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		List<Product> products = new LinkedList<Product>();
		for (Long productId : productIds) {
			products.add(LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "products", Product.class, productId));
		}
		return Response.status(Response.Status.OK).entity(products).build();
	}
}
