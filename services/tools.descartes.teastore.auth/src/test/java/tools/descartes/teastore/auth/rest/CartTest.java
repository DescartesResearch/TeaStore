package tools.descartes.teastore.auth.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import tools.descartes.teastore.entities.User;
import tools.descartes.teastore.entities.message.SessionBlob;
import tools.descartes.teastore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;

/**
 * Abstract base for testing of the stores user actions funtionality.
 * 
 * @author Simon
 *
 */
public class CartTest extends AbstractStoreRestTest {

  /**
   * Tests for the loggin, logout and isloggedin functionality.
   * 
   * @throws JsonProcessingException exception if json can not be processed
   */
  @Test
  public void runTest() throws JsonProcessingException {
    mockProduct106();
    mockProduct107();
    mockInvalidProduct();
    mockUser1();
    mockInvalidUser();
    mockCreateOrderItems();
    mockCreateOrder();

    SessionBlob notLoggedIn = new SessionBlob();

    notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 106L);
    Assert.assertEquals(1, notLoggedIn.getOrderItems().size());
    Assert.assertEquals(106, notLoggedIn.getOrderItems().get(0).getProductId());
    Assert.assertEquals(1, notLoggedIn.getOrderItems().get(0).getQuantity());

    notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 107L);
    Assert.assertEquals(2, notLoggedIn.getOrderItems().size());
    Assert.assertEquals(107, notLoggedIn.getOrderItems().get(1).getProductId());
    Assert.assertEquals(1, notLoggedIn.getOrderItems().get(1).getQuantity());

    notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 106L);
    Assert.assertEquals(2, notLoggedIn.getOrderItems().size());
    Assert.assertEquals(2, notLoggedIn.getOrderItems().get(0).getQuantity());

    notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 107L);
    Assert.assertEquals(2, notLoggedIn.getOrderItems().size());
    Assert.assertEquals(2, notLoggedIn.getOrderItems().get(1).getQuantity());

    try {
      LoadBalancedStoreOperations.addProductToCart(notLoggedIn, -1L);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    notLoggedIn = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, 7);
    Assert.assertEquals(7, notLoggedIn.getOrderItems().get(0).getQuantity());

    try {
      LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, -1);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }

    try {
      LoadBalancedStoreOperations.updateQuantity(notLoggedIn, -1L, 7);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    try {
      LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 108L, 7);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
    Assert.assertEquals(1, notLoggedIn.getOrderItems().size());
    Assert.assertEquals(107, notLoggedIn.getOrderItems().get(0).getProductId());

    try {
      LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 107L);
    Assert.assertEquals(0, notLoggedIn.getOrderItems().size());

    notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 107L);

    try {
      LoadBalancedStoreOperations.placeOrder(notLoggedIn, "", "", "", "", "2015-12-12", -1L, "");
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    SessionBlob loggedIn = new SessionBlob();
    loggedIn = LoadBalancedStoreOperations.login(loggedIn, "user1", "password");

    loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 106L);
    Assert.assertEquals(1, loggedIn.getOrderItems().size());
    Assert.assertEquals(106, loggedIn.getOrderItems().get(0).getProductId());
    Assert.assertEquals(1, loggedIn.getOrderItems().get(0).getQuantity());

    loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 107L);
    Assert.assertEquals(2, loggedIn.getOrderItems().size());
    Assert.assertEquals(107, loggedIn.getOrderItems().get(1).getProductId());
    Assert.assertEquals(1, loggedIn.getOrderItems().get(1).getQuantity());

    loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 106L);
    Assert.assertEquals(2, loggedIn.getOrderItems().size());
    Assert.assertEquals(2, loggedIn.getOrderItems().get(0).getQuantity());

    loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 107L);
    Assert.assertEquals(2, loggedIn.getOrderItems().size());
    Assert.assertEquals(2, loggedIn.getOrderItems().get(1).getQuantity());

    try {
      LoadBalancedStoreOperations.addProductToCart(loggedIn, -1L);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, 7);
    Assert.assertEquals(7, loggedIn.getOrderItems().get(0).getQuantity());

    try {
      loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, -1);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }

    try {
      LoadBalancedStoreOperations.updateQuantity(loggedIn, -1L, 7);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    try {
      LoadBalancedStoreOperations.updateQuantity(loggedIn, 108L, 7);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
    Assert.assertEquals(1, loggedIn.getOrderItems().size());
    Assert.assertEquals(107, loggedIn.getOrderItems().get(0).getProductId());

    try {
      LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 107L);
    Assert.assertEquals(0, loggedIn.getOrderItems().size());

    try {
      LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L, "");
      Assert.fail();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 107L);
    loggedIn = LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L,
        "");
    Assert.assertTrue(loggedIn != null);
    Assert.assertTrue(loggedIn.getOrderItems().isEmpty());
    Assert.assertTrue(loggedIn.getOrder().getAddress1() == null);
  }

  private void mockUser1() {
    User u = new User();
    u.setEmail("asdas@asda.de");
    u.setRealName("asdas asdasd");
    u.setUserName("user1");
    u.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
    u.setId(1231245125);
    mockValidGetRestCall(u, "/tools.descartes.teastore.persistence/rest/users/name/user1");
  }

  private void mockInvalidUser() {
    mockInValidGetRestCall(Response.Status.NOT_FOUND,
        "/tools.descartes.teastore.persistence/rest/users/name/user/-1");
  }

  /**
   * Returns id of newly created object.
   */
  private void mockCreateOrderItems() {
    mockValidPostRestCall(8, "/tools.descartes.teastore.persistence/rest/orderitems");
  }

  /**
   * Returns id of newly created object.
   */
  private void mockCreateOrder() {
    mockValidPostRestCall(7, "/tools.descartes.teastore.persistence/rest/orders");
  }
}
