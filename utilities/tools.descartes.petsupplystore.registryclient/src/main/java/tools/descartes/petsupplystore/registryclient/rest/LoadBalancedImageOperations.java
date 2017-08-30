package tools.descartes.petsupplystore.registryclient.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;

/**
 * Wrapper for rest operations.
 * @author mediocre comments --> Simon, good code --> Norbert
 *
 */
public final class LoadBalancedImageOperations {

	private LoadBalancedImageOperations() {
		
	}
	
	/**
	 * Retrieves image for a product.
	 * @param product product.
	 * @return image for product
	 */
	public static String getProductImage(Product product) {
		return getProductImage(product, ImageSize.FULL);
	}
	
	/**
	 * Gets product image.
	 * @param product product.
	 * @param size target size
	 * @return image for product with target size
	 */
	public static String getProductImage(Product product, ImageSize size) {
		HashMap<Long, ImageSize> img = new HashMap<>();
		img.put(product.getId(), size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getProductImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		try {
			HashMap<Long, String> encoded = r.readEntity(new GenericType<HashMap<Long, String>>() { });
			return encoded.get(product.getId());	
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Gets preview images for a series of products.
	 * @param products List of products
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductPreviewImages(List<Product> products) {
		return getProductImages(products, ImageSize.PREVIEW);
	}
	
	/**
	 * Gets preview images for a series of products with target image size.
	 * @param products list of products
	 * @param size target size
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductImages(List<Product> products, ImageSize size) {
		HashMap<Long, ImageSize> img = new HashMap<>();
		for (Product p : products) {
			img.put(p.getId(), size);
		}
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getProductImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		try {
			return r.readEntity(new GenericType<HashMap<Long, String>>() { });
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Retrieves web image.
	 * @param name name of image.
	 * @param size target size
	 * @return image
	 */
	public static String getWebImage(String name, ImageSize size) {
		HashMap<String, ImageSize> img = new HashMap<>();
		img.put(name, size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getWebImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		try {
			HashMap<String, String> encoded = r.readEntity(new GenericType<HashMap<String, String>>() { });
			return encoded.get(name);
		} catch (NullPointerException e) {
			return null;
		}
	}

	
	/**
	 * Retrieves a series of web image.
	 * @param names list of name of image.
	 * @param size target size
	 * @return HashMap containing requested images.
	 */
	public static HashMap<String, String> getWebImages(List<String> names, ImageSize size) {
		HashMap<String, ImageSize> img = new HashMap<>();
		for (String name : names) {
			img.put(name, size);
		}
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getWebImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		try {
			return r.readEntity(new GenericType<HashMap<String, String>>() { });
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Regenerates images.
	 * @return List of status codes.
	 */
	public static List<Integer> regenerateImages() {
		List<Response> r = ServiceLoadBalancer.multicastRESTOperation(Service.IMAGE, "image", null, 
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("regenerateImages").request().get());
		if (r == null) {
			return new ArrayList<Integer>();
		}
		return r.stream()
				.filter(response -> response != null)
				.map(response -> response.getStatus())
				.collect(Collectors.toList());
	}
}
