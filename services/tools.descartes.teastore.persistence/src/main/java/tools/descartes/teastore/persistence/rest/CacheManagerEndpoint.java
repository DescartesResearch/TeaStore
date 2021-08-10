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

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tools.descartes.teastore.persistence.repository.CacheManager;

/**
 * REST endpoint for cache clearing.
 * @author Joakim von Kistowski
 */
@Path("cache")
@Produces("text/plain")
public final class CacheManagerEndpoint {

	/**
	 * Clears the cache for the class.
	 * @param className fully qualified class name.
	 * @return Status Code 200 and cleared class name if clear succeeded, 404 if it didn't.
	 */
	@DELETE
	@Path("/class/{class}")
	public Response clearClassCache(@PathParam("class") final String className) {
		boolean classfound = true;
		try {
			Class<?> entityClass = Class.forName(className);
			CacheManager.MANAGER.clearLocalCacheOnly(entityClass);
		} catch (Exception e) {
			classfound = false;
		}
		if (classfound) {
			return Response.ok(className).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	
	/**
	 * Clears the entire cache.
	 * @return Status Code 200 and "cleared" text if clear succeeded, 404 if it didn't.
	 */
	@DELETE
	@Path("/cache")
	public Response clearAllCaches() {
		CacheManager.MANAGER.clearLocalCacheOnly();
		return Response.ok("cleared").build();
	}
	
	/**
	 * Closes and resets the EMF.
	 * @return Status Code 200 and "clearedEMF" text if reset succeeded, 404 if it didn't.
	 */
	@DELETE
	@Path("/emf")
	public Response clearEMF() {
		CacheManager.MANAGER.resetLocalEMF();
		return Response.ok("clearedEMF").build();
	}
}
