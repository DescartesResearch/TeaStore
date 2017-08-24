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
package tools.descartes.petsupplystore.persistence.rest;

import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.persistence.repository.DataGenerator;

/**
 * Persistence endpoint for generating new database content.
 * @author Joakim von Kistowski
 *
 */
@Path("generatedb")
@Produces("text/plain")
public class DatabaseGenerationEndpoint {

	/**
	 * Drop database and create a new one.
	 * @param categories Number of new categories.
	 * @param products Number of new products per category.
	 * @param users Number of new users.
	 * @param orders Number of max orders per user.
	 * @return Status OK.
	 */
	@GET
	public Response generateDataBase(
			@QueryParam("categories") final Integer categories,
			@QueryParam("products") final Integer products,
			@QueryParam("users") final Integer users,
			@QueryParam("orders") final Integer orders) {
		DataGenerator.GENERATOR.dropAndCreateTables();
		int categoryCount = parseQuery(categories, DataGenerator.SMALL_DB_CATEGORIES);
		int productCount = parseQuery(products, DataGenerator.SMALL_DB_PRODUCTS_PER_CATEGORY);
		int userCount = parseQuery(users, DataGenerator.SMALL_DB_USERS);
		int maxOrderCount = parseQuery(orders, DataGenerator.SMALL_DB_MAX_ORDERS_PER_USER);
		Executors.newSingleThreadScheduledExecutor().execute(
				() -> DataGenerator.GENERATOR.generateDatabaseContent(categoryCount,
						productCount, userCount, maxOrderCount));
		String message = "Creating database with "
				+ categoryCount + " categories, "
				+ productCount + " products per category, "
				+ userCount + " users, "
				+ maxOrderCount + " max orders per user.";
		return Response.ok(message).build();
	}
	
	private int parseQuery(Integer param, int defaultValue) {
		if (param == null) {
			return defaultValue;
		}
		return param;
	}
	
	/**
	 * Returns the is finished flag for database generation.
	 * @return True, if generation is finished; false, if in progress.
	 */
	@GET
	@Path("finished")
	public Response isFinshed() {
		boolean isFinished = DataGenerator.GENERATOR.getGenerationFinishedFlag();
		return Response.ok(String.valueOf(isFinished)).build();
	}
}
