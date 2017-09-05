package tools.descartes.petsupplystore.registryclient.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petsupplystore.entities.ImageSize;
import tools.descartes.petsupplystore.entities.ImageSizePreset;
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
		return getProductImage(product, ImageSizePreset.FULL.getSize());
	}
	
	/**
	 * Gets product image.
	 * @param product product.
	 * @param size target size
	 * @return image for product with target size
	 */
	public static String getProductImage(Product product, ImageSize size) {
		return getProductImages(Stream.of(product).collect(Collectors.toList()), size)
				.getOrDefault(product.getId(), "");	
	}
	
	/**
	 * Gets preview images for a series of products.
	 * @param products List of products
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductPreviewImages(List<Product> products) {
		return getProductImages(products, ImageSizePreset.PREVIEW.getSize());
	}
	
	/**
	 * Gets preview images for a series of products with target image size.
	 * @param products list of products
	 * @param size target size
	 * @return HashMap containing all preview images
	 */
	public static HashMap<Long, String> getProductImages(List<Product> products, ImageSize size) {
		HashMap<Long, String> img = new HashMap<>();
		for (Product p : products) {
			img.put(p.getId(), size.toString());
		}
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getProductImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		if (r == null) {
			return new HashMap<Long, String>();
		}
		
		HashMap<Long, String> result = r.readEntity(new GenericType<HashMap<Long, String>>() { });
		if (result == null) {
			return new HashMap<Long, String>();
		}
		return result;
	}
	
	/**
	 * Retrieves web image.
	 * @param name name of image.
	 * @param size target size
	 * @return image
	 */
	public static String getWebImage(String name, ImageSize size) {
		return getWebImages(Stream.of(name).collect(Collectors.toList()), size).getOrDefault(name, "");	
	}

	
	/**
	 * Retrieves a series of web image.
	 * @param names list of name of image.
	 * @param size target size
	 * @return HashMap containing requested images.
	 */
	public static HashMap<String, String> getWebImages(List<String> names, ImageSize size) {
		HashMap<String, String> img = new HashMap<>();
		for (String name : names) {
			img.put(name, size.toString());
		}
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getWebImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		if (r == null) {
			return new HashMap<String, String>();
		}
		
		HashMap<String, String> result = r.readEntity(new GenericType<HashMap<String, String>>() { });
		if (result == null) {
			return new HashMap<String, String>();
		}
		return result;
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
