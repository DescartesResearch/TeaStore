package tools.descartes.teastore.registryclient.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import tools.descartes.teastore.registryclient.Service;
import tools.descartes.teastore.registryclient.loadbalancers.LoadBalancerTimeoutException;
import tools.descartes.teastore.registryclient.loadbalancers.ServiceLoadBalancer;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Container class for the static calls to the Store service.
 * 
 * @author Simon
 *
 */
public final class LoadBalancedStoreOperations {

  private LoadBalancedStoreOperations() {

  }

  /**
   * Persists order in database.
   * 
   * @param blob
   *          Sessionblob
   * @param addressName
   *          adress
   * @param address1
   *          adress
   * @param address2
   *          adress
   * @param creditCardCompany
   *          creditcard
   * @param creditCardExpiryDate
   *          creditcard
   * @param creditCardNumber
   *          creditcard
   * @param totalPriceInCents
   *          totalPrice
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return empty SessionBlob
   */
  public static SessionBlob placeOrder(SessionBlob blob, String addressName, String address1,
      String address2, String creditCardCompany, String creditCardExpiryDate,
      long totalPriceInCents, String creditCardNumber)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "useractions",
        Product.class,
        client -> ResponseWrapper.wrap(HttpWrapper
            .wrap(client.getEndpointTarget().path("placeorder")
                .queryParam("addressName", addressName).queryParam("address1", address1)
                .queryParam("address2", address2).queryParam("creditCardCompany", creditCardCompany)
                .queryParam("creditCardNumber", creditCardNumber)
                .queryParam("creditCardExpiryDate", creditCardExpiryDate)
                .queryParam("totalPriceInCents", totalPriceInCents))
            .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

  /**
   * Login if name and pw are correct.
   * 
   * @param blob
   *          SessionBlob
   * @param name
   *          username
   * @param password
   *          user password
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return SessionBlob with login information if login was successful
   */
  public static SessionBlob login(SessionBlob blob, String name, String password)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "useractions",
        Product.class,
        client -> ResponseWrapper.wrap(HttpWrapper
            .wrap(client.getEndpointTarget().path("login").queryParam("name", name)
                .queryParam("password", password))
            .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

  /**
   * Logs user out.
   * 
   * @param blob
   *          SessionBlob
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return SessionBlob without user information
   */
  public static SessionBlob logout(SessionBlob blob)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "useractions",
        Product.class,
        client -> ResponseWrapper.wrap(HttpWrapper.wrap(client.getEndpointTarget().path("logout"))
            .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

  /**
   * Checks if user is logged in.
   * 
   * @param blob
   *          SessionBlob
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return true if user is logged in
   */
  public static boolean isLoggedIn(SessionBlob blob)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "useractions",
        Product.class,
        client -> ResponseWrapper
            .wrap(HttpWrapper.wrap(client.getEndpointTarget().path("isloggedin"))
                .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class) != null;
  }

  /**
   * Adds product to cart. if the item is already in the cart, the quantity is
   * increased.
   * 
   * @param blob
   *          SessionBlob
   * @param pid
   *          ProductId
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return Sessionblob containing product
   */
  public static SessionBlob addProductToCart(SessionBlob blob, long pid)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "cart", Product.class,
        client -> ResponseWrapper
            .wrap(HttpWrapper.wrap(client.getEndpointTarget().path("add").path("" + pid))
                .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

  /**
   * Removes product from cart.
   * 
   * @param blob
   *          Sessionblob
   * @param pid
   *          productid
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return Sessionblob without product
   */
  public static SessionBlob removeProductFromCart(SessionBlob blob, long pid)
      throws NotFoundException, LoadBalancerTimeoutException {
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "cart", Product.class,
        client -> ResponseWrapper
            .wrap(HttpWrapper.wrap(client.getEndpointTarget().path("remove").path("" + pid))
                .post(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

  /**
   * Updates quantity of item in cart.
   * 
   * @param blob
   *          Sessionblob
   * @param pid
   *          productid of item
   * @param quantity
   *          target quantity
   * @throws NotFoundException
   *           If 404 was returned.
   * @throws LoadBalancerTimeoutException
   *           On receiving the 408 status code and on repeated load balancer
   *           socket timeouts.
   * @return Sessionblob with updated quantity
   */
  public static SessionBlob updateQuantity(SessionBlob blob, long pid, int quantity)
      throws NotFoundException, LoadBalancerTimeoutException {
    if (quantity < 1) {
      throw new IllegalArgumentException("Quantity has to be larger than 1");
    }
    Response r = ServiceLoadBalancer.loadBalanceRESTOperation(Service.AUTH, "cart", Product.class,
        client -> ResponseWrapper.wrap(HttpWrapper
            .wrap(client.getEndpointTarget().path("" + pid).queryParam("quantity", quantity))
            .put(Entity.entity(blob, MediaType.APPLICATION_JSON), Response.class)));
    return RestUtil.readThrowAndOrClose(r, SessionBlob.class);
  }

}
