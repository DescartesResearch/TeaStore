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
package tools.descartes.teastore.persistence.rest;

import java.util.concurrent.Executors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.persistence.repository.CacheManager;
import tools.descartes.teastore.persistence.repository.DataGenerator;
import tools.descartes.teastore.registryclient.RegistryClient;

/**
 * Persistence endpoint for generating new database content.
 * @author Joakim von Kistowski
 *
 */
@Path("generatedb")
public class DatabaseGenerationEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseGenerationEndpoint.class);

	/**
	 * Drop database and create a new one.
	 * @param categories Number of new categories.
	 * @param products Number of new products per category.
	 * @param users Number of new users.
	 * @param orders Number of max orders per user.
	 * @return Status OK. Returns {@value DataGenerator.MAINTENANCE_STATUS_CODE}
	 * if in maintenance mode.
	 */
	@GET
	public Response generateDataBase(
			@QueryParam("categories") final Integer categories,
			@QueryParam("products") final Integer products,
			@QueryParam("users") final Integer users,
			@QueryParam("orders") final Integer orders) {
		LOG.info("Received database generation command for Persistence at "
			+ RegistryClient.getClient().getMyServiceInstanceServer() + ".");
		if (DataGenerator.GENERATOR.isMaintenanceMode()) {
			return Response.status(DataGenerator.MAINTENANCE_STATUS_CODE).build();
		}
		DataGenerator.GENERATOR.setMaintenanceModeGlobal(true);
		LOG.info("Global maintenance mode enabled.");
		DataGenerator.GENERATOR.dropAndCreateTables();
		LOG.info("Finished dropping tables and re-initializing database schmema.");
		int categoryCount = parseQuery(categories, DataGenerator.SMALL_DB_CATEGORIES);
		int productCount = parseQuery(products, DataGenerator.SMALL_DB_PRODUCTS_PER_CATEGORY);
		int userCount = parseQuery(users, DataGenerator.SMALL_DB_USERS);
		int maxOrderCount = parseQuery(orders, DataGenerator.SMALL_DB_MAX_ORDERS_PER_USER);
		LOG.info("Initializing database creation with "
				+ categoryCount + " categories, "
				+ productCount + " products per category, "
				+ userCount + " users, "
				+ maxOrderCount + " max orders per user.");
		Executors.newSingleThreadScheduledExecutor().execute(() -> {
			DataGenerator.GENERATOR.generateDatabaseContent(categoryCount,
					productCount, userCount, maxOrderCount);
			LOG.info("Finished database generation.");
			CacheManager.MANAGER.resetRemoteEMFs();
			LOG.info("Finished resetting all Persistence service instances.");
			DataGenerator.GENERATOR.setMaintenanceModeGlobal(false);
			LOG.info("Done. Maintenance mode disabled.");
		});
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
	 * Also returns false if the persistence provider is in maintenance mode.
	 * @return True, if generation is finished; false, if in progress.
	 */
	@GET
	@Path("finished")
	public Response isFinshed() {
		if (DataGenerator.GENERATOR.getGenerationFinishedFlag()) {
			return Response.ok(true).build();
		} else {
			return Response.serverError().entity(false).build();
		}
	}

	/**
	 * Disables or enables the maintenance mode.
	 * Persistence providers in maintenance mode return 503 on almost anything.
	 * @param maintenanceMode Send true to enable, false to disable.
	 * @return 404 if message body was missing. 200, otherwise.
	 */
	@POST
	@Path("maintenance")
	public Response setMaintenanceMode(final Boolean maintenanceMode) {
		if (maintenanceMode == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		DataGenerator.GENERATOR.setMaintenanceModeInternal(maintenanceMode);
		return Response.ok().build();
	}

	/**
	 * Returns the is maintenance flag. Only to be used by other persistence providers.
	 * @return True, if in maintenance; false, otherwise.
	 */
	@GET
	@Path("maintenance")
	public Response isMaintenance() {
		return Response.ok(DataGenerator.GENERATOR.isMaintenanceMode()).build();
	}
}
