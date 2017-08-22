package tools.descartes.petstore.registryclient.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petstore.entities.ImageSize;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;

public class LoadBalancedImageOperations {

	private LoadBalancedImageOperations() {
		
	}
	
	public static String getProductImage(Product product) {
		return getProductImage(product, ImageSize.FULL);
	}
	
	public static String getProductImage(Product product, ImageSize size) {
		HashMap<Long, ImageSize> img = new HashMap<>();
		img.put(product.getId(), size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getProductImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		HashMap<Long, String> encoded = r.readEntity(new GenericType<HashMap<Long, String>>() { });
		return encoded.get(product.getId());	
	}
	
	public static HashMap<Long, String> getProductPreviewImages(List<Product> products) {
		return getProductImages(products, ImageSize.PREVIEW);
	}
	
	public static HashMap<Long, String> getProductImages(List<Product> products, ImageSize size) {
		HashMap<Long, ImageSize> img = new HashMap<>();
		for (Product p : products)
			img.put(p.getId(), size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getProductImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		return r.readEntity(new GenericType<HashMap<Long, String>>() { });
	}
	
	public static String getWebImage(String name, ImageSize size) {
		HashMap<String, ImageSize> img = new HashMap<>();
		img.put(name, size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getWebImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		HashMap<String, String> encoded = r.readEntity(new GenericType<HashMap<String, String>>() { });
		return encoded.get(name);
	}
	
	public static HashMap<String, String> getWebImages(List<String> names, ImageSize size) {
		HashMap<String, ImageSize> img = new HashMap<>();
		for (String name : names)
			img.put(name, size);
		
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.IMAGE, "image", HashMap.class,
				client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("getWebImages").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(img, MediaType.APPLICATION_JSON)));
		
		return r.readEntity(new GenericType<HashMap<String, String>>() { });	
	}
	
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
