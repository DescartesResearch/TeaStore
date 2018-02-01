package tools.descartes.petsupplystore.registryclient.rest;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.entities.User;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.Service;
import tools.descartes.petsupplystore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.petsupplystore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.petsupplystore.rest.NotFoundException;
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
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return List of categories
	 */
	public static List<Category> getCategories() throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"categories", Category.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		List<Category> entity = null;
		try {
			entity = r.readEntity(new GenericType<List<Category>>() { });
			
		} finally {
			if (r != null) {
				r.close();
			}
		}
		RestUtil.throwCommonExceptions(r);
		return entity;
	}
	
	/**
	 * Gets category by id.
	 * @param cid categoryid
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return category
	 */
	public static Category getCategory(long cid) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"categories", Category.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("" + cid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return RestUtil.readThrowAndOrClose(r, Category.class);
	}
	
	/**
	 * Gets product by id.
	 * @param pid productid
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return product
	 */
	public static Product getProduct(long pid) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return RestUtil.readThrowAndOrClose(r, Product.class);
	}

	/**
	 * Gets ads.
	 * @param blob SessionBlob
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return product
	 */
	public static List<Product> getAdvertisements(SessionBlob blob)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("ads")
				.queryParam("uid", blob.getUID())
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob.getOrderItems(), MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readListThrowAndOrCloseProduct(r);
	}
	
	/**
	 * Gets ads.
	 * @param blob SessionBlob
	 * @param pid pid of currentProduct
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return product
	 */
	public static List<Product> getAdvertisements(SessionBlob blob, long pid)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("ads").queryParam("pid", pid)
				.queryParam("uid", blob.getUID())
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob.getOrderItems(), MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readListThrowAndOrCloseProduct(r);
	}
	
	/**
	 * Gets all products from one category on page if every page has X articles per page.
	 * @param cid categoryid
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return Number of products
	 */
	public static int getNumberOfProducts(long cid) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", List.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("category").path("" + cid).path("totalNumber")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return RestUtil.readThrowAndOrClose(r, Integer.class);
	}
	
	/**
	 * Gets all products from one category on page if every page has X articles per page.
	 * @param cid categoryid
	 * @param page pagenumber
	 * @param articlesPerPage number of articles per page
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return List of products
	 */
	public static List<Product> getProducts(long cid, int page, int articlesPerPage)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"products", List.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("category").path("" + cid).queryParam("page", page)
				.queryParam("articlesPerPage", articlesPerPage).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).get());
		return RestUtil.readListThrowAndOrCloseProduct(r);
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
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return empty SessionBlob
	 */
	public static SessionBlob placeOrder(SessionBlob blob, String addressName, String address1, 
			String address2, String creditCardCompany, String creditCardExpiryDate, long totalPriceInCents,
			String creditCardNumber) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("placeorder")
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
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Login if name and pw are correct.
	 * @param blob SessionBlob
	 * @param name username
	 * @param password user password
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return SessionBlob with login information if login was successful
	 */
	public static SessionBlob login(SessionBlob blob, String name, String password)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("login").queryParam("name", name)
				.queryParam("password", password).request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Logs user out.
	 * @param blob SessionBlob
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return SessionBlob without user information
	 */
	public static SessionBlob logout(SessionBlob blob) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("logout").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Checks if user is logged in.
	 * @param blob SessionBlob
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return true if user is logged in
	 */
	public static boolean isLoggedIn(SessionBlob blob) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"useractions", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("isloggedin").request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class) != null;
	}
	
	/**
	 * Adds product to cart. if the item is already in the cart, the quantity is increased.
	 * @param blob SessionBlob
	 * @param pid ProductId
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return Sessionblob containing product
	 */
	public static SessionBlob addProductToCart(SessionBlob blob, long pid)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("add").path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Removes product from cart.
	 * @param blob Sessionblob
	 * @param pid productid
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return Sessionblob without product
	 */
	public static SessionBlob removeProductFromCart(SessionBlob blob, long pid)
			throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("remove").path("" + pid).request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Updates quantity of item in cart.
	 * @param blob Sessionblob
	 * @param pid productid of item
	 * @param quantity target quantity
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return Sessionblob with updated quantity
	 */
	public static SessionBlob updateQuantity(SessionBlob blob, long pid, int quantity)
			throws NotFoundException, LoadBalancerTimeoutException {
		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity has to be larger than 1");
		}
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"cart", Product.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("" + pid).queryParam("quantity", quantity)
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class));
		return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
	}
	
	/**
	 * Gets user via id.
	 * @param uid user id
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return user
	 */
	public static User getUser(long uid) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"users", User.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("" + uid)
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.get());
		return RestUtil.readThrowAndOrClose(r, User.class);
	}
	
	/**
	 * Gets all orders for a user.
	 * @param uid userid
	 * @throws NotFoundException If 404 was returned.
	 * @throws LoadBalancerTimeoutException On receiving the 408 status code
     * and on repeated load balancer socket timeouts.
	 * @return List of orders
	 */
	public static List<Order> getOrdersForUser(long uid) throws NotFoundException, LoadBalancerTimeoutException {
		Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.STORE,
				"users", User.class, client -> client.getService().path(client.getApplicationURI())
				.path(client.getEndpointURI()).path("" + uid).path("orders")
				.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.get());
		return RestUtil.readListThrowAndOrCloseOrder(r);
	}
}

