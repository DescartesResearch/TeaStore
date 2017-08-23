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
package tools.descartes.petstore.recommender.rest;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.recommender.IRecommender;
import tools.descartes.petstore.recommender.RecommenderSelector;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petstore.registryclient.rest.LoadBalancedCRUDOperations;

/**
 * REST endpoint to trigger the (re)training of the Recommender.
 * 
 * @author Johannes Grohmann
 *
 */
@Path("train")
@Produces({ "text/plain", "application/json" })
public class TrainEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(TrainEndpoint.class);
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
			long number = retrieveDataAndRetrain();
			long time = System.currentTimeMillis() - start;
			if (number != -1) {
				return Response.ok("The (re)train was succesfully done. It took " + time + "ms and " + number
						+ " of Orderitems were retrieved from the database.").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(500)
				.entity("The (re)trainprocess failed.").build();
	}

	/**
	 * Triggers the training of the recommendation algorithm. It retrieves all data
	 * {@link OrderItem}s and all {@link Order}s from the database entity and is
	 * therefore both very network and computation time intensive. <br>
	 * This method must be called before the {@link RecommendEndpoint} is usable, as
	 * the {@link IRecommender} will throw an
	 * {@link UnsupportedOperationException}.<br/>
	 * Waits for the Database to be ready before training but returns immediately.
	 * Calling this method twice will trigger a retraining.
	 * 
	 * @return Returns a {@link Response} with
	 *         {@link javax.servlet.http.HttpServletResponse#SC_OK}.
	 */
	@GET
	@Path("async")
	public Response trainAsync() {
		ScheduledExecutorService asyncTrainExecutor = Executors.newSingleThreadScheduledExecutor();
		asyncTrainExecutor.scheduleWithFixedDelay(() -> {
			String finished = ServiceLoadBalancer.loadBalanceRESTOperation(
					Service.PERSISTENCE, "generatedb", String.class,
					client -> client.getEndpointTarget().path("finished").request(MediaType.TEXT_PLAIN)
					.get().readEntity(String.class));
			if (finished.equals("true")) {
				long number = retrieveDataAndRetrain();
				if (number != -1) {
					asyncTrainExecutor.shutdown();
				}
			} else {
				LOG.info("Waiting for DB before retraining.");
			}
		}, 0, 3, TimeUnit.SECONDS); 
		return Response.ok("training").build();
	}
	
	
	
	/**
	 * Connects via REST to the database and retrieves all {@link OrderItem}s and
	 * all {@link Order}s. Then, it triggers the training of the recommender.
	 * 
	 * @return The number of elements retrieved from the database or -1 if the
	 *         process failed.
	 */
	public static long retrieveDataAndRetrain() {
		List<OrderItem> items = null;
		List<Order> orders = null;
		try {
			items = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "orderitems", OrderItem.class, -1, -1);
			if (items == null || items.isEmpty()) {
				return -1;
			}
			orders = LoadBalancedCRUDOperations.getEntities(Service.PERSISTENCE, "orders", Order.class, -1, -1);
			if (orders == null || orders.isEmpty()) {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		RecommenderSelector.getInstance().train(items, orders);
		return items.size() + orders.size();

	}
}
