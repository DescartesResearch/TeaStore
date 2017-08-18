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
package tools.descartes.petstore.persistence;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.petstore.entities.Category;
import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.OrderItem;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.entities.User;
import tools.descartes.petstore.persistence.rest.CategoryEndpoint;
import tools.descartes.petstore.persistence.rest.OrderEndpoint;
import tools.descartes.petstore.persistence.rest.OrderItemEndpoint;
import tools.descartes.petstore.persistence.rest.ProductEndpoint;
import tools.descartes.petstore.persistence.rest.UserEndpoint;
import tools.descartes.petstore.persistence.domain.CategoryRepository;
import tools.descartes.petstore.rest.NonBalancedCRUDOperations;
import tools.descartes.petstore.rest.RESTClient;

/**
 * Test for the OrderEndpoint and OrderItemEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class OrderItemEndpointTest {
	
	private static final String CONTEXT = "/test";
	
	private Tomcat testTomcat;
	private String testWorkingDir = System.getProperty("java.io.tmpdir");
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		testTomcat = new Tomcat();
		testTomcat.setPort(0);
		testTomcat.setBaseDir(testWorkingDir);
		Context context = testTomcat.addWebapp(CONTEXT, testWorkingDir);
		ResourceConfig restServletConfig = new ResourceConfig();
		restServletConfig.register(ProductEndpoint.class);
		restServletConfig.register(CategoryEndpoint.class);
		restServletConfig.register(OrderItemEndpoint.class);
		restServletConfig.register(UserEndpoint.class);
		restServletConfig.register(OrderEndpoint.class);
		ServletContainer restServlet = new ServletContainer(restServletConfig);
		testTomcat.addServlet(CONTEXT, "restServlet", restServlet);
		context.addServletMappingDecoded("/rest/*", "restServlet");
		testTomcat.start();
		System.out.println("Started Testing Tomcat at port " + getTomcatPort());
		int dbinitializationSize = CategoryRepository.REPOSITORY.getAllEntities().size();
		System.out.println("DB initialized at size " + dbinitializationSize);
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {
		//create category, and product
		RESTClient<Category> categoryClient = new RESTClient<Category>("http://localhost:"
		+ getTomcatPort() + CONTEXT + "/", "rest", "categories", Category.class);
		RESTClient<Product> productClient = new RESTClient<Product>("http://localhost:"
				+ getTomcatPort() + CONTEXT + "/", "rest", "products", Product.class);
		RESTClient<User> userClient = new RESTClient<User>("http://localhost:"
				+ getTomcatPort() + CONTEXT + "/", "rest", "users", User.class);
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
				+ getTomcatPort() + CONTEXT + "/", "rest", "orderitems", OrderItem.class);
		RESTClient<Order> orderClient = new RESTClient<Order>("http://localhost:"
				+ getTomcatPort() + CONTEXT + "/", "rest", "orders", Order.class);
		
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
		Assert.assertFalse(NonBalancedCRUDOperations.sendEntityForUpdate(itemClient, -1L, creationItem));
		
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
		if (testTomcat.getServer() != null && testTomcat.getServer().getState() != LifecycleState.DESTROYED) {
	        if (testTomcat.getServer().getState() != LifecycleState.STOPPED) {
	        	testTomcat.stop();
	        }
	        testTomcat.destroy();
	    }
	}
	
	private int getTomcatPort() {
		return testTomcat.getConnector().getLocalPort();
	}
	
}
