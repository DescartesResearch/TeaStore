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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedCRUDOperations;
import tools.descartes.petsupplystore.rest.NotFoundException;
import tools.descartes.petsupplystore.rest.TimeoutException;

/**
 * Rest endpoint for the store categories.
 * 
 * @author Simon
 */
@Path("categories")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public class StoreCategoriesREST {

	/**
	 * get all categories.
	 * 
	 * @return Response containing all categories
	 */
	@GET
	public Response getCategories() {
		List<Category> categories;
		try {
			categories = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "categories", Category.class, -1,
					-1);
		} catch (TimeoutException e) {
			return Response.status(408).build();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(Response.Status.OK).entity(categories).build();
	}

	/**
	 * Gets category by category id.
	 * 
	 * @param cid
	 *            category id
	 * @return Response containing category
	 */
	@GET
	@Path("{cid}")
	public Response getCategory(@PathParam("cid") final long cid) {
		Category category;
		try {
			category = LoadBalancedCRUDOperations.getEntity(Service.PERSISTENCE, "categories", Category.class,
					cid);
		} catch (TimeoutException e) {
			return Response.status(408).build();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(Response.Status.OK).entity(category).build();
	}

}
