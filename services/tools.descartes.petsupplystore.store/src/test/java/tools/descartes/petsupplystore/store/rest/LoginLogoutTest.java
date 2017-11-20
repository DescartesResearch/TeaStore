package tools.descartes.petsupplystore.store.rest;


import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.mail.iap.Response;

import tools.descartes.petsupplystore.entities.User;
import tools.descartes.petsupplystore.entities.message.SessionBlob;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Abstract base for testing of the stores user actions funtionality.
 * @author Simon
 *
 */
public class LoginLogoutTest extends AbstractStoreRestTest {

	/**
	 * Tests for the loggin, logout and isloggedin functionality.
	 * @throws JsonProcessingException 
	 */
	@Test
	public void runTest() throws JsonProcessingException {
		mockUser1();
		mockProduct106();
		mockCreateOrder();
		mockCreateOrderItems();
		
		SessionBlob blob = new SessionBlob();
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		mockInValidGetRestCall(Status.NOT_FOUND, "/tools.descartes.petsupplystore.persistence/rest/users/name/notauser");
		blob = LoadBalancedStoreOperations.login(blob, "notauser", "notapassword");
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));

		blob = LoadBalancedStoreOperations.login(blob, "user1", "password");
		Assert.assertTrue(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		blob = LoadBalancedStoreOperations.logout(blob);
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		blob = LoadBalancedStoreOperations.logout(blob);
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));

		blob = LoadBalancedStoreOperations.login(blob, "user1", "password");
		blob = LoadBalancedStoreOperations.addProductToCart(blob, 106);
		Assert.assertTrue(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		blob = LoadBalancedStoreOperations.removeProductFromCart(blob, 106);
		Assert.assertTrue(LoadBalancedStoreOperations.isLoggedIn(blob));

		blob = LoadBalancedStoreOperations.addProductToCart(blob, 106);
		blob = LoadBalancedStoreOperations.updateQuantity(blob, 106, 2);
		Assert.assertTrue(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		blob = LoadBalancedStoreOperations.placeOrder(blob, "", "", "", "", "2015-12-12", -1L, "");
		Assert.assertTrue(LoadBalancedStoreOperations.isLoggedIn(blob));
		
		blob = LoadBalancedStoreOperations.logout(blob);
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));
	}

	private void mockCreateOrderItems() {
		mockValidPostRestCall(Response.OK, "/tools.descartes.petsupplystore.persistence/rest/orderitems");
	}

	private void mockCreateOrder() {
		mockValidPostRestCall(7, "/tools.descartes.petsupplystore.persistence/rest/orders");
	}

	private void mockUser1() {
		User u = new User();
		u.setEmail("asdas@asda.de");
		u.setRealName("asdas asdasd");
		u.setUserName("user1");
		u.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
		u.setId(1231245125);
		mockValidGetRestCall(u, "/tools.descartes.petsupplystore.persistence/rest/users/name/user1");
	}
}
