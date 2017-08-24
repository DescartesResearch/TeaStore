package tools.descartes.petsupplystore.store.rest;

import org.junit.Assert;
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
	 */
	protected void runTest() {
		SessionBlob blob = new SessionBlob();
		
		Assert.assertFalse(LoadBalancedStoreOperations.isLoggedIn(blob));
		
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
}
