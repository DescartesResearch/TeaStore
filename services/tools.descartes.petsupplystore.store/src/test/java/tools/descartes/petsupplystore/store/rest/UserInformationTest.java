package tools.descartes.petsupplystore.store.rest;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.descartes.petsupplystore.entities.Order;
import tools.descartes.petsupplystore.entities.User;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Tests for the user information retrieval.
 * @author Simon
 *
 */
public class UserInformationTest extends AbstractStoreRestTest {

	/**JsonProcessingException
	 * Tests for the queries about user information.
	 * @throws  
	 */
	@Test
	public void runTest() throws JsonProcessingException {
		mockUser509();
		mockOrdersForUser509();
		
		User u = LoadBalancedStoreOperations.getUser(509);
		Assert.assertTrue(u != null);
		
		try {
			LoadBalancedStoreOperations.getUser(-1);
			Assert.fail();
		} catch (Exception e) {}
		
		List<Order> orders = LoadBalancedStoreOperations.getOrdersForUser(509);
		Assert.assertTrue(orders != null && orders.size() != 0);

		try {
			LoadBalancedStoreOperations.getOrdersForUser(-1);
			Assert.fail();
		} catch (Exception e) {}
	}
	
	private void mockOrdersForUser509() {
		Order o = new Order();
		List<Order> os = new LinkedList<Order>();
		os.add(o);
 		mockValidGetRestCall(os, "/tools.descartes.petsupplystore.persistence/rest/orders/user/509");
 		mockValidGetRestCall(null, "/tools.descartes.petsupplystore.persistence/rest/orders/user/-1");
	}

	private void mockUser509() {
		User u = new User();
		u.setEmail("asdas@asda.de");
		u.setRealName("asdas asdasd");
		u.setUserName("user509");
		u.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
		u.setId(1231245125);
		mockValidGetRestCall(u, "/tools.descartes.petsupplystore.persistence/rest/users/509");
		mockValidGetRestCall(null, "/tools.descartes.petsupplystore.persistence/rest/users/-1");
	}
}
