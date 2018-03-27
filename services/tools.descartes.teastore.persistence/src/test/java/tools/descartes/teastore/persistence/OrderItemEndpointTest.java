/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.teastore.persistence.rest.CategoryEndpoint;
import tools.descartes.teastore.persistence.rest.OrderEndpoint;
import tools.descartes.teastore.persistence.rest.OrderItemEndpoint;
import tools.descartes.teastore.persistence.rest.ProductEndpoint;
import tools.descartes.teastore.persistence.rest.UserEndpoint;
import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

/**
 * Test for the OrderEndpoint and OrderItemEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class OrderItemEndpointTest {
	
	
	private TomcatTestHandler handler;
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		handler = new TomcatTestHandler(ProductEndpoint.class, CategoryEndpoint.class,
				OrderItemEndpoint.class, UserEndpoint.class, OrderEndpoint.class);
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {
		//create category, and product
		RESTClient<Category> categoryClient = new RESTClient<Category>("http://localhost:"
		+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "categories", Category.class);
		RESTClient<Product> productClient = new RESTClient<Product>("http://localhost:"
				+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "products", Product.class);
		RESTClient<User> userClient = new RESTClient<User>("http://localhost:"
				+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "users", User.class);
		Category cat = new Category();
		cat.setName("Category");
		cat.setDescription("Category Description");
		long categoryId = NonBalancedCRUDOperations.sendEntityForCreation(categoryClient, cat);
		Product creationProduct = new Product();
		creationProduct.setCategoryId(categoryId);
		creationProduct.setName("rest product");
		creationProduct.setDescription("rest description");
		creationProduct.setListPriceInCents(999);
		long productId = NonBalancedCRUDOperations.sendEntityForCreation(productClient, creationProduct);
		User user = new User();
		user.setRealName("realname");
		user.setUserName("username");
		user.setEmail("email");
		user.setPassword("password");
		long userId = NonBalancedCRUDOperations.sendEntityForCreation(userClient, user);
		
		//open connection
		RESTClient<OrderItem> itemClient = new RESTClient<OrderItem>("http://localhost:"
				+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "orderitems", OrderItem.class);
		RESTClient<Order> orderClient = new RESTClient<Order>("http://localhost:"
				+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "orders", Order.class);
		
		//get initial order item table size
		int initialItems = NonBalancedCRUDOperations.getEntities(itemClient, -1, -1).size();
		int initialOrders = NonBalancedCRUDOperations.getEntities(orderClient, -1, -1).size();
		
		//create an order
		Order creationOrder = new Order();
		creationOrder.setUserId(userId);
		creationOrder.setAddressName("address");
		creationOrder.setTotalPriceInCents(5);
		long orderId = NonBalancedCRUDOperations.sendEntityForCreation(orderClient, creationOrder);
		Assert.assertTrue(orderId > 0);
		//create invalid order
		creationOrder.setUserId(0L);
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForCreation(orderClient, creationOrder) < 0);
		
		//get and update order
		Order order = NonBalancedCRUDOperations.getEntity(orderClient, orderId);
		Assert.assertEquals(5, order.getTotalPriceInCents());
		order.setTotalPriceInCents(2);
		NonBalancedCRUDOperations.sendEntityForUpdate(orderClient, orderId, order);
		Assert.assertEquals(2, NonBalancedCRUDOperations.getEntity(orderClient, orderId).getTotalPriceInCents());
		
		//get some orders
		Assert.assertEquals(initialOrders + 1, NonBalancedCRUDOperations.getEntities(orderClient, -1, -1).size());
		Assert.assertEquals(1, NonBalancedCRUDOperations.getEntities(orderClient, "user", userId, -1, 2).size());
		
		//create order item
		OrderItem creationItem = new OrderItem();
		creationItem.setOrderId(orderId);
		creationItem.setProductId(productId);
		creationItem.setQuantity(5);
		creationItem.setUnitPriceInCents(2);
		long itemId = NonBalancedCRUDOperations.sendEntityForCreation(itemClient, creationItem);
		Assert.assertTrue(itemId >= 0);
		
		//create invalid order item
		creationItem.setProductId(-2L);
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForCreation(itemClient, creationItem) <= 0);
		
		//update order item
		creationItem.setUnitPriceInCents(6);
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(itemClient, itemId, creationItem));
		try {
			NonBalancedCRUDOperations.sendEntityForUpdate(itemClient, -1L, creationItem);
			Assert.fail();
		} catch (NotFoundException e) {
			//don't fail
		}
		
		//receive order item
		OrderItem recEnt = NonBalancedCRUDOperations.getEntity(itemClient, itemId);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getUnitPriceInCents(), 6);
		Assert.assertEquals(recEnt.getQuantity(), 5);
		
		//get some order items
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, 0, 1).size(), 1);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, -1, -1).size(), 1 + initialItems);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, "order", orderId, -1, -1).size(), 1);
		
		//get order item with products
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, "product",
				productId, -1, -1).size(), 1);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, "product",
				99999, -1, -1).size(), 0);
		
		//delete order item
		Assert.assertTrue(NonBalancedCRUDOperations.deleteEntity(itemClient, itemId));
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(itemClient, "product",
				productId, -1, -1).size(), 0);
		Assert.assertTrue(NonBalancedCRUDOperations.getEntities(itemClient, -1, -1).stream()
				.noneMatch(te -> te.getId() == itemId));
		
		//delete order
		Assert.assertTrue(NonBalancedCRUDOperations.deleteEntity(orderClient, orderId));
		Assert.assertTrue(NonBalancedCRUDOperations.getEntities(orderClient, -1, -1).stream()
				.noneMatch(te -> te.getId() == orderId));
	}
	
	/**
	 * Dismantles the embedded Tomcat.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@After
	public void dismantle() throws Throwable {
		handler.dismantleAll();
	}
	
}
