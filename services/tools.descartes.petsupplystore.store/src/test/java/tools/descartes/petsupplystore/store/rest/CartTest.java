package tools.descartes.petsupplystore.store.rest;

import org.junit.Assert;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.descartes.petsupplystore.entities.User;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;
import tools.descartes.petsupplystore.rest.NotFoundException;



/**
 * Abstract base for testing of the stores user actions funtionality.
 * @author Simon
 *
 */
public class CartTest extends AbstractStoreRestTest {
	
	/**
	 * Tests for the loggin, logout and isloggedin functionality.
	 * @throws JsonProcessingException 
	 */
	@Test
	public void runTest() throws JsonProcessingException {
		mockProduct106();
		mockProduct107();
		mockUser1();
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
		} catch (NotFoundException e) {}
		
		notLoggedIn = LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, 7);
		Assert.assertEquals(7, notLoggedIn.getOrderItems().get(0).getQuantity());
		
		try {
			LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 106L, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			LoadBalancedStoreOperations.updateQuantity(notLoggedIn, -1L, 7);
			Assert.fail();
		} catch (NotFoundException e) {}

		try {
			LoadBalancedStoreOperations.updateQuantity(notLoggedIn, 108L, 7);
			Assert.fail();
		} catch (NotFoundException e) {}
		
		notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
		Assert.assertEquals(1, notLoggedIn.getOrderItems().size());
		Assert.assertEquals(107, notLoggedIn.getOrderItems().get(0).getProductId());

		try {
			LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 106L);
			Assert.fail();
		} catch (NotFoundException e) {}
		
		notLoggedIn = LoadBalancedStoreOperations.removeProductFromCart(notLoggedIn, 107L);
		Assert.assertEquals(0, notLoggedIn.getOrderItems().size());

		
		notLoggedIn = LoadBalancedStoreOperations.addProductToCart(notLoggedIn, 107L);
		
		try {
			LoadBalancedStoreOperations.placeOrder(notLoggedIn, "", "", "", "", "2015-12-12", -1L, "");
			Assert.fail();
		} catch (NotFoundException e) {}
		
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
		} catch (NotFoundException e) {}
		
		loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, 7);
		Assert.assertEquals(7, loggedIn.getOrderItems().get(0).getQuantity());
		
		try {
			loggedIn = LoadBalancedStoreOperations.updateQuantity(loggedIn, 106L, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			LoadBalancedStoreOperations.updateQuantity(loggedIn, -1L, 7);
			Assert.fail();
		} catch (NotFoundException e) {}

		try {
			LoadBalancedStoreOperations.updateQuantity(loggedIn, 108L, 7);
			Assert.fail();
		} catch (NotFoundException e) {}
		
		loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
		Assert.assertEquals(1, loggedIn.getOrderItems().size());
		Assert.assertEquals(107, loggedIn.getOrderItems().get(0).getProductId());

		try {
			LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 106L);
			Assert.fail();
		} catch (NotFoundException e) {}
		
		loggedIn = LoadBalancedStoreOperations.removeProductFromCart(loggedIn, 107L);
		Assert.assertEquals(0, loggedIn.getOrderItems().size());

		try {
			LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L, "");
			Assert.fail();
		} catch (NotFoundException e) {}
		
		loggedIn = LoadBalancedStoreOperations.addProductToCart(loggedIn, 107L);
		loggedIn = LoadBalancedStoreOperations.placeOrder(loggedIn, "", "", "", "", "2015-12-12", -1L, "");
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
		mockValidGetRestCall(u, "/tools.descartes.petsupplystore.persistence/rest/users/name/user1");
		mockValidGetRestCall(null, "/tools.descartes.petsupplystore.persistence/rest/users/name/user-1");
	}

	private void mockCreateOrderItems() {
		mockValidPostRestCall(200, "/tools.descartes.petsupplystore.persistence/rest/orderitems");
	}

	private void mockCreateOrder() {
		mockValidPostRestCall(7, "/tools.descartes.petsupplystore.persistence/rest/orders");
	}
}
