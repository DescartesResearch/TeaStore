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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedRecommenderOperations;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;

/**
 * Rest endpoint for the store product.
 * @author Simon
 */
@Path("products")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class StoreProductREST {
	
	/**
	 * Gets product by product id.
	 * @param pid product id
	 * @return Response containing product
	 */
	@GET
	@Path("{pid}")
	public Response getProduct(@PathParam("pid") final Long pid) {
		Product product = LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "products", Product.class, pid);
		return Response.status(Response.Status.OK).entity(product).build();
	}
	
	/**
	 * Get advertisement.
	 * @param order current cart state
	 * @param pid pid
	 * @return Response containing product
	 */
	@POST
	@Path("ads")
	public Response getAdvertisement(List<OrderItem> orderItems, @QueryParam("pid") final Long pid) {
		List<OrderItem> items = new LinkedList<>();
		if (pid != null) {
			OrderItem oi = new OrderItem();
			oi.setProductId(pid);
			oi.setQuantity(1);
			items.add(oi);
		}
		items.addAll(orderItems);
		List<Long> productIds = LoadBalancedRecommenderOperations.getRecommendations(items);
		List<Product> products = new LinkedList<Product>();
		for (Long productId: productIds) {
			products.add(LoadBalancedStoreOperations.getProduct(productId));
		}
		return Response.status(Response.Status.OK).entity(products).build();
	}
	
	/**
	 * Gets all products from a category page of a set size.
	 * @param cid category id
	 * @param page page number
	 * @param articlesPerPage number of articles per page
	 * @return Response containing List of products
	 */ 
	@GET
	@Path("category/{cid}")
	public Response getProducts(@PathParam("cid") final Long cid, @QueryParam("page") int page,
			@QueryParam("articlesPerPage") int articlesPerPage) {
		List<Product> products = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE,
				"products", Product.class, "category", cid, (page - 1) * articlesPerPage, articlesPerPage);
		return Response.status(Response.Status.OK).entity(products).build();
	}
	
	/**
	 * Gets all products from a category page of a set size.
	 * @param cid category id
	 * @return Response containing List of products
	 */ 
	@GET
	@Path("category/{cid}/totalNumber")
	public Response getNumberOfProducts(@PathParam("cid") final Long cid) {
		String s = ServiceLoadBalancer.loadBalanceRESTOperation(Service.PERSISTENCE,
				"products", Product.class,
				client -> client.getEndpointTarget().path("count").path(String.valueOf(cid))
				.request(MediaType.TEXT_PLAIN).get().readEntity(String.class));
		long result = Long.parseLong(s);
		return Response.status(Response.Status.OK).entity(result).build();
	}
}
