package tools.descartes.petstore.registryclient.rest;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petstore.entities.Category;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
/**
 * Container class for the static calls to the Store service.
 * @author Simon
 *
 */
public final class LoadBalancedRecommenderOperations {

	private LoadBalancedRecommenderOperations() {
		
	}
	
	/**
	 * Gets recommendations.
	 * @param order list of order items
	 * @return List of recommended order ids
	 */
	public static List<Long> getRecommendations(List<OrderItem> order) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.RECOMMENDER,
				"recommend", Category.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(order, MediaType.APPLICATION_JSON)));
		return r.readEntity(new GenericType<List<Long>>() { });
	}
}

