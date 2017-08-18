package tools.descartes.petstore.registryclient.rest;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tools.descartes.petstore.entities.Category;
import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.entities.User;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.Service;
import tools.descartes.petstore.registryclient.loadbalancers.ServiceLoadBalancer;
/**
 * Container class for the static calls to the Store service.
 * @author Simon
 *
 */
public final class LoadBalancedStoreOperations {

	private LoadBalancedStoreOperations() {
		
	}
	
	/**
	 * Gets all categories.
	 * @return List of categories
	 */
	public static List<Category> getCategories() {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"categories", Category.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return r.readEntity(new GenericType<List<Category>>() { });
	}
	
	/**
	 * Gets category by id.
	 * @param cid categoryid
	 * @return category
	 */
	public static Category getCategory(long cid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"categories", Category.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("" + cid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return r.readEntity(Category.class);
	}
	
	/**
	 * Gets product by id.
	 * @param pid productid
	 * @return product
	 */
	public static Product getProduct(long pid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return r.readEntity(Product.class);
	}
	
	/**
	 * Gets ads.
	 * @param order Order details
	 * @param number number of ads
	 * @return product
	 */
	public static List<Product> getAdvertisements(SessionBlob blob, int number) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("ads").queryParam("number", number)
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob.getOrderItems(), MediaType.APPLICATION_JSON), Response.class));
		return r.readEntity(new GenericType<List<Product>>() { });
	}
	
	/**
	 * Gets all products from one category on page if every page has X articles per page.
	 * @param cid categoryid
	 * @return Number of products
	 */
	public static int getNumberOfProducts(long cid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", List.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("category").path("" + cid).path("totalNumber")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return r.readEntity(int.class);
	}
	
	/**
	 * Gets all products from one category on page if every page has X articles per page.
	 * @param cid categoryid
	 * @param page pagenumber
	 * @param articlesPerPage number of articles per page
	 * @return List of products
	 */
	public static List<Product> getProducts(long cid, int page, int articlesPerPage) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", List.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("category").path("" + cid).queryParam("page", page)
				.queryParam("articlesPerPage", articlesPerPage).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return r.readEntity(new GenericType<List<Product>>() { });
	}
	
	/**
	 * Persists order in database.
	 * @param blob Sessionblob
	 * @param addressName adress
	 * @param address1 adress
	 * @param address2 adress
	 * @param creditCardCompany creditcard
	 * @param creditCardExpiryDate creditcard
	 * @param creditCardNumber creditcard
	 * @param totalPriceInCents totalPrice
	 * @return empty SessionBlob
	 */
	public static SessionBlob placeOrder(SessionBlob blob, String addressName, String address1, 
			String address2, String creditCardCompany, String creditCardExpiryDate, long totalPriceInCents,
			String creditCardNumber) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("placeorder")
				.queryParam("addressName", addressName)
				.queryParam("address1", address1)
				.queryParam("address2", address2)
				.queryParam("creditCardCompany", creditCardCompany)
				.queryParam("creditCardNumber", creditCardNumber)
				.queryParam("creditCardExpiryDate", creditCardExpiryDate)
				.queryParam("totalPriceInCents", totalPriceInCents)
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		if (r.getStatus() == 200)  {
			return r.readEntity(SessionBlob.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Login if name and pw are correct.
	 * @param blob SessionBlob
	 * @param name username
	 * @param password user password
	 * @return SessionBlob with login information if login was successful
	 */
	public static SessionBlob login(SessionBlob blob, String name, String password) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("login").queryParam("name", name)
				.queryParam("password", password).request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return r.readEntity(SessionBlob.class);
	}
	
	/**
	 * Logs user out.
	 * @param blob SessionBlob
	 * @return SessionBlob without user information
	 */
	public static SessionBlob logout(SessionBlob blob) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("logout").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return r.readEntity(SessionBlob.class);
	}
	
	/**
	 * Checks if user is logged in.
	 * @param blob SessionBlob
	 * @return true if user is logged in
	 */
	public static boolean isLoggedIn(SessionBlob blob) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("isloggedin").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		SessionBlob validatedBlob = r.readEntity(SessionBlob.class);
		return validatedBlob != null;
	}
	
	/**
	 * Adds product to cart. if the item is already in the cart, the quantity is increased.
	 * @param blob SessionBlob
	 * @param pid ProductId
	 * @return Sessionblob containing product
	 */
	public static SessionBlob addProductToCart(SessionBlob blob, long pid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("add").path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		if (r.getStatus() == 200)  {
			return r.readEntity(SessionBlob.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Removes product from cart.
	 * @param blob Sessionblob
	 * @param pid productid
	 * @return Sessionblob without product
	 */
	public static SessionBlob removeProductFromCart(SessionBlob blob, long pid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("remove").path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		if (r.getStatus() == 200) {
			return r.readEntity(SessionBlob.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Updates quantity of item in cart.
	 * @param blob Sessionblob
	 * @param pid productid of item
	 * @param quantity target quantity
	 * @return Sessionblob with updated quantity
	 */
	public static SessionBlob updateQuantity(SessionBlob blob, long pid, int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity has to be larger than 1");
		}
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("" + pid).queryParam("quantity", quantity)
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		if (r.getStatus() == 200) {
			return r.readEntity(SessionBlob.class);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets user via id.
	 * @param uid user id
	 * @return user
	 */
	public static User getUser(long uid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"users", User.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("" + uid)
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.get());
		return r.readEntity(User.class);
	}
	
	/**
	 * Gets all orders for a user.
	 * @param uid userid
	 * @return List of orders
	 */
	public static List<Order> getOrdersForUser(long uid) {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"users", User.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEnpointURI()).path("" + uid).path("orders")
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.get());
		return r.readEntity(new GenericType<List<Order>>() { });
	}
}

