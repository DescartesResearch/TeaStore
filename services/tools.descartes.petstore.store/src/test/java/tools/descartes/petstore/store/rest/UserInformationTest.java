package tools.descartes.petstore.store.rest;

import java.util.List;

import org.junit.Assert;

import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.User;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;



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
		
		User u2 = LoadBalancedStoreOperations.getUser(-1);
		Assert.assertTrue(u2 == null);
		
		List<Order> categories = LoadBalancedStoreOperations.getOrdersForUser(509);
		Assert.assertTrue(categories != null && categories.size() != 0);
		
		List<Order> categories2 = LoadBalancedStoreOperations.getOrdersForUser(-1);
		Assert.assertTrue(categories2.size() == 0);
	}
}
