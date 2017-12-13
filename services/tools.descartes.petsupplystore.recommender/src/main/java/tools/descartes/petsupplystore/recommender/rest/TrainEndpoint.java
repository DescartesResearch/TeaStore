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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.recommender.algorithm.IRecommender;
import tools.descartes.petsupplystore.recommender.servlet.TrainingSynchronizer;

/**
 * REST endpoint to trigger the (re)training of the Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
@Path("train")
@Produces({ "text/plain", "application/json" })
public class TrainEndpoint {

	/**
	 * Triggers the training of the recommendation algorithm. It retrieves all data
	 * {@link OrderItem}s and all {@link Order}s from the database entity and is
	 * therefore both very network and computation time intensive. <br>
	 * This method must be called before the {@link RecommendEndpoint} is usable, as
	 * the {@link IRecommender} will throw an
	 * {@link UnsupportedOperationException}.<br>
	 * Calling this method twice will trigger a retraining.
	 * 
	 * @return Returns a {@link Response} with
	 *         {@link javax.servlet.http.HttpServletResponse#SC_OK} or with
	 *         {@link javax.servlet.http.HttpServletResponse#SC_INTERNAL_SERVER_ERROR},
	 *         if the operation failed.
	 */
	@GET
	public Response train() {
		try {
			long start = System.currentTimeMillis();
			long number = TrainingSynchronizer.getInstance().retrieveDataAndRetrain();
			long time = System.currentTimeMillis() - start;
			if (number != -1) {
				return Response.ok("The (re)train was succesfully done. It took " + time + "ms and " + number
						+ " of Orderitems were retrieved from the database.").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				.entity("The (re)trainprocess failed.").build();
	}

	/**
	 * Returns the last time stamp, which was considered at the training of this
	 * instance.
	 * 
	 * @return Returns a {@link Response} with
	 *         {@link javax.servlet.http.HttpServletResponse#SC_OK} containing the
	 *         maximum considered time as String or with
	 *         {@link javax.servlet.http.HttpServletResponse#SC_INTERNAL_SERVER_ERROR},
	 *         if the operation failed.
	 */
	@GET
	@Path("timestamp")
	public Response getTimeStamp() {
		long time = TrainingSynchronizer.getInstance().getMaxTime();
		if (TrainingSynchronizer.getInstance().getMaxTime() == Long.MIN_VALUE) {
			return Response.status(Response.Status.PRECONDITION_FAILED.getStatusCode())
					.entity("The collection of the current maxTime was not possible.").build();
		}
		return Response.ok(TrainingSynchronizer.getInstance().getMaxTime()).build();
	}
}
