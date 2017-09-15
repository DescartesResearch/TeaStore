package tools.descartes.petsupplystore.store.rest;

import java.util.List;

import org.junit.Assert;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.User;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Tests for the user information retrieval.
 * @author Simon
 *
 */
public class UserInformationTest extends AbstractStoreRestTest {

	/**
	 * Tests for the queries about user information.
	 */
	protected void runTest() {
		User u = LoadBalancedStoreOperations.getUser(509);
		Assert.assertTrue(u != null);
		
		try {
			LoadBalancedStoreOperations.getUser(-1);
			Assert.fail();
		} catch (Exception e) {}
		
		List<Order> categories = LoadBalancedStoreOperations.getOrdersForUser(509);
		Assert.assertTrue(categories != null && categories.size() != 0);
		
		List<Order> categories2 = LoadBalancedStoreOperations.getOrdersForUser(-1);
		Assert.assertTrue(categories2.size() == 0);
	}
}
