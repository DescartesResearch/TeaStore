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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.petsupplystore.rest.NotFoundException;
import tools.descartes.petsupplystore.rest.TimeoutException;
import tools.descartes.petsupplystore.store.security.SHASecurityProvider;

/**
 * Rest endpoint for the store cart.
 * @author Simon
 */
@Path("cart")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class StoreCartREST {
	
	/**
	 * Adds product to cart. If the product is already in the cart the quantity is increased.
	 * @param blob Sessionblob
	 * @param pid productid
	 * @return Response containing session blob with updated cart
	 */
	@POST
	@Path("add/{pid}")
	public Response addProductToCart(SessionBlob blob, @PathParam("pid") final Long pid) {
		Product product;
		try {
			product = LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "products", Product.class, pid);
		} catch (TimeoutException e) {
			return Response.status(408).build();
		} catch (NotFoundException e) {
			return Response.status(408).build();
		}
		
		for (OrderItem oItem: blob.getOrderItems()) {
			if (oItem.getProductId() == pid) {
				oItem.setQuantity(oItem.getQuantity() + 1);
				return Response.status(Response.Status.OK).entity(blob).build();
			}
		}
		OrderItem item = new OrderItem();
		item.setProductId(pid);
		item.setQuantity(1);
		item.setUnitPriceInCents(product.getListPriceInCents());
		blob.getOrderItems().add(item);
		blob = new SHASecurityProvider().secure(blob);
		return Response.status(Response.Status.OK).entity(blob).build();
	}
	
	/**
	 * Remove product from cart.
	 * @param blob Sessionblob
	 * @param pid product id
	 * @return Response containing Sessionblob with updated cart
	 */
	@POST
	@Path("remove/{pid}")
	public Response removeProductFromCart(SessionBlob blob, @PathParam("pid") final Long pid) {
		OrderItem toRemove = null;
		for (OrderItem item: blob.getOrderItems()) {
			if (item.getProductId() == pid) {
				toRemove = item;
			}
		}
		if (toRemove != null) {
			blob.getOrderItems().remove(toRemove);
			blob = new SHASecurityProvider().secure(blob);
			return Response.status(Response.Status.OK).entity(blob).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	/**
	 * Updates quantity of product in cart.
	 * @param blob Sessionblob
	 * @param pid Productid
	 * @param quantity New quantity
	 * @return Response containing Sessionblob with updated cart
	 */
	@PUT
	@Path("{pid}")
	public Response updateQuantity(SessionBlob blob, @PathParam("pid") final Long pid,
			@QueryParam("quantity") int quantity) {
		for (OrderItem item: blob.getOrderItems()) {
			if (item.getProductId() == pid) {
				item.setQuantity(quantity);
				blob = new SHASecurityProvider().secure(blob);
				return Response.status(Response.Status.OK).entity(blob).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
}
