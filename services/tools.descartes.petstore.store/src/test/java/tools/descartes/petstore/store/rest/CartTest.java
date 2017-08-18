package tools.descartes.petstore.store.rest;

import org.junit.Assert;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Abstract base for testing of the stores user actions funtionality.
 * @author Simon
 *
 */
public class CartTest extends AbstractStoreRestTest {

	/**
	 * Tests for the loggin, logout and isloggedin functionality.
	 */
	protected void runTest() {
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
		
		SessionBlob notFound = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, -1L);
		Assert.assertTrue(notFound == null);
		
		notLoggedIn = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, 7);
		Assert.assertEquals(7, notLoggedIn.getOrderItems().get(0).getQuantity());
		
		try {
			notLoggedIn = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
		
		notFound = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, -1L, 7);
		Assert.assertTrue(notFound == null);
		
		notFound = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 108L, 7);
		Assert.assertTrue(notFound == null);
		
		notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
		Assert.assertEquals(1, notLoggedIn.getOrderItems().size());
		Assert.assertEquals(107, notLoggedIn.getOrderItems().get(0).getProductId());
		
		notFound = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
		Assert.assertTrue(notFound == null);
		
		notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 107L);
		Assert.assertEquals(0, notLoggedIn.getOrderItems().size());

		
		notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 107L);
		notFound = LoadBalancedStoreOperations.placeOrder(notLoggedIn, "", "", "", "", "2015-12-12", -1L, "");
		Assert.assertTrue(notFound == null);
		
		
		
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
		
		notFound = LoadBalancedStoreOperations.addProductToCart(loggedIn, -1L);
		Assert.assertTrue(notFound == null);
		
		loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, 7);
		Assert.assertEquals(7, loggedIn.getOrderItems().get(0).getQuantity());
		
		try {
			loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
		
		notFound = LoadBalancedStoreOperations.updateQuantity(loggedIn, -1L, 7);
		Assert.assertTrue(notFound == null);
		
		notFound = LoadBalancedStoreOperations.updateQuantity(loggedIn, 108L, 7);
		Assert.assertTrue(notFound == null);
		
		loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
		Assert.assertEquals(1, loggedIn.getOrderItems().size());
		Assert.assertEquals(107, loggedIn.getOrderItems().get(0).getProductId());
		
		notFound = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
		Assert.assertTrue(notFound == null);
		
		loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 107L);
		Assert.assertEquals(0, loggedIn.getOrderItems().size());
		
		notFound = LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L, "");
		Assert.assertTrue(notFound == null);
		
		loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 107L);
		loggedIn = LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L, "");
		Assert.assertTrue(loggedIn != null);
		Assert.assertTrue(loggedIn.getOrderItems().isEmpty());
		Assert.assertTrue(loggedIn.getOrder().getAddress1() == null);
		
	}
}
