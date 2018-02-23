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
import tools.descartes.petsupplystore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.registryclient.util.NotFoundException;

/**
 * Container class for the static calls to the Store service.
 * 
 * @author Simon
 *
 */
public final class LoadBalancedRecommenderOperations {

	private LoadBalancedRecommenderOperations() {

	}

	/**
	 * Gets recommendations.
	 * 
	 * @param order
	 *            list of order items
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return List of recommended order ids
	 */
	public static List<Long> getRecommendations(List<OrderItem> order, Long uid)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.RECOMMENDER, "recommend", Category.class,
				client -> HttpWrapper.wrap(client.getEndpointTarget().queryParam("uid", uid))
						.post(Entity.entity(order, MediaType.APPLICATION_JSON)));
		if (r != null) {
			if (r.getStatus() < 400) {
				return r.readEntity(new GenericType<List<Long>>() {
				});
			} else {
				r.bufferEntity();
			}
		}
		return new ArrayList<>();
	}
}
