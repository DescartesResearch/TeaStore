package tools.descartes.teastore.registryclient.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;
import tools.descartes.teastore.entities.Product;

/**
 * Wrapper for rest operations.
 * 
 * @author mediocre comments --> Simon, good code --> Norbert
 *
 */
public final class LoadBalancedImageOperations {

	private LoadBalancedImageOperations() {

	}

	/**
	 * Retrieves image for a product.
	 * 
	 * @param product
	 *            product.
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return image for product
	 */
	public static String getProductImage(Product product) throws NotFoundException, LoadBalancerTimeoutException {
		return getProductImage(product, ImageSizePreset.FULL.getSize());
	}

	/**
	 * Gets product image.
	 * 
	 * @param product
	 *            product.
	 * @param size
	 *            target size
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return image for product with target size
	 */
	public static String getProductImage(Product product, ImageSize size)
			throws NotFoundException, LoadBalancerTimeoutException {
		return getProductImages(Stream.of(product).collect(Collectors.toList()), size).getOrDefault(product.getId(),
				"");
	}

	/**
	 * Gets preview images for a series of products.
	 * 
	 * @param products
	 *            List of products
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductPreviewImages(List<Product> products)
			throws NotFoundException, LoadBalancerTimeoutException {
		return getProductImages(products, ImageSizePreset.PREVIEW.getSize());
	}

	/**
	 * Gets preview images for a series of products with target image size.
	 * 
	 * @param products
	 *            list of products
	 * @param size
	 *            target size
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductImages(List<Product> products, ImageSize size)
			throws NotFoundException, LoadBalancerTimeoutException {
		HashMap<Long, String> img = new HashMap<>();
		for (Product p : products) {
			img.put(p.getId(), size.toString());
		}

		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> ResponseWrapper.wrap(HttpWrapper.wrap(client.getEndpointTarget().path("getProductImages"))
						.post(Entity.entity(img, MediaType.APPLICATION_JSON))));

		if (r == null) {
			return new HashMap<Long, String>();
		}

		HashMap<Long, String> result = null;
		if (r.getStatus() < 400) {
			result = r.readEntity(new GenericType<HashMap<Long, String>>() {
			});
		} else {
			// buffer all entities so that the connections are released to the connection
			// pool
			r.bufferEntity();
		}
		if (result == null) {
			return new HashMap<Long, String>();
		}
		return result;
	}

	/**
	 * Retrieves web image.
	 * 
	 * @param name
	 *            name of image.
	 * @param size
	 *            target size
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return image
	 */
	public static String getWebImage(String name, ImageSize size)
			throws NotFoundException, LoadBalancerTimeoutException {
		return getWebImages(Stream.of(name).collect(Collectors.toList()), size).getOrDefault(name, "");
	}

	/**
	 * Retrieves a series of web image.
	 * 
	 * @param names
	 *            list of name of image.
	 * @param size
	 *            target size
	 * @throws NotFoundException
	 *             If 404 was returned.
	 * @throws LoadBalancerTimeoutException
	 *             On receiving the 408 status code and on repeated load balancer
	 *             socket timeouts.
	 * @return HashMap containing requested images.
	 */
	public static HashMap<String, String> getWebImages(List<String> names, ImageSize size)
			throws NotFoundException, LoadBalancerTimeoutException {
		HashMap<String, String> img = new HashMap<>();
		for (String name : names) {
			img.put(name, size.toString());
		}

		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> ResponseWrapper.wrap(HttpWrapper.wrap(client.getEndpointTarget().path("getWebImages"))
						.post(Entity.entity(img, MediaType.APPLICATION_JSON))));

		if (r == null) {
			return new HashMap<String, String>();
		}

		HashMap<String, String> result = null;
		if (r.getStatus() < 400) {
			result = r.readEntity(new GenericType<HashMap<String, String>>() {
			});
		} else {
			// buffer all entities so that the connections are released to the connection
			// pool
			r.bufferEntity();
		}
		if (result == null) {
			return new HashMap<String, String>();
		}
		return result;
	}

	/**
	 * Regenerates images.
	 * 
	 * @return List of status codes.
	 */
	public static List<Integer> regenerateImages() {
		List<Response> r = ServiceLoadBalancer.multicastRESTOperation(Service.IMAGE, "image", null,
				client -> client.getEndpointTarget().path("regenerateImages").request().get());
		if (r == null) {
			return new ArrayList<Integer>();
		}
		List<Integer> statuses = r.stream().filter(response -> response != null).map(response -> response.getStatus())
				.collect(Collectors.toList());
		// buffer all entities so that the connections are released to the connection
		// pool
		r.stream().filter(response -> response != null).forEach(response -> response.bufferEntity());
		return statuses;
	}
}
