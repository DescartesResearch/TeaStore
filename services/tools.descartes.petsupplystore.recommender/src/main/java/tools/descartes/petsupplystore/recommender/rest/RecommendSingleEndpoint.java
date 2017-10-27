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
package tools.descartes.petsupplystore.recommender.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.recommender.algorithm.RecommenderSelector;

/**
 * Recommender REST endpoint for single recommendation.
 * 
 * @author Johannes Grohmann
 *
 */
@Path("recommendsingle")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class RecommendSingleEndpoint {

	/**
	 * Return a list of all {@link Product}s, that are recommended for a customer
	 * buying the given {@link OrderItem}. <br>
	 * 
	 * The returning list does not contain the {@link Product} of the respective
	 * {@link OrderItem}. It might be empty, however.
	 * 
	 * @param item
	 *            An {@link OrderItem} to use as recommendator. Must not be null
	 * @return List of {@link Long} objects, containing all {@link Product} IDs that
	 *         are recommended to add to the cart, or an INTERNALSERVERERROR, if the
	 *         recommendation failed.
	 */
	@POST
	public Response recommend(OrderItem item) {
		if (item == null) {
			throw new NullPointerException("OrderItem must not be null.");
		}
		LinkedList<OrderItem> list = new LinkedList<OrderItem>();
		list.add(item);
		List<Long> recommended = RecommenderSelector.getInstance().recommendProducts(list);
		return Response.ok().entity(recommended).build();
	}
}
