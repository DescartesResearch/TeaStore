package tools.descartes.petsupplystore.registryclient.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.OrderItem;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
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
				.path(client.getEndpointURI()).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(order, MediaType.APPLICATION_JSON)));
		try {
			return r.readEntity(new GenericType<List<Long>>() { });
		} catch (NullPointerException e) {
			return new ArrayList<>();
		}
	}
}

