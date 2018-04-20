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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import org.junit.Assert;

import tools.descartes.teastore.persistence.domain.CategoryRepository;
import tools.descartes.teastore.persistence.domain.OrderItemRepository;
import tools.descartes.teastore.persistence.domain.OrderRepository;
import tools.descartes.teastore.persistence.domain.PersistenceCategory;
import tools.descartes.teastore.persistence.domain.PersistenceOrderItem;
import tools.descartes.teastore.persistence.domain.PersistenceProduct;
import tools.descartes.teastore.persistence.domain.ProductRepository;
import tools.descartes.teastore.persistence.domain.UserRepository;
import tools.descartes.teastore.persistence.repository.EMFManagerInitializer;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Order;
import tools.descartes.teastore.entities.OrderItem;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.entities.User;

/**
 * Test for the ProductEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class RepositoryTest {
	
	private CategoryRepository catRepo;
	private ProductRepository prodRepo;
	private OrderItemRepository orderItemRepo;
	private OrderRepository orderRepo;
	private UserRepository userRepo;
	
	/**
	 * Setup the test.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		EMFManagerInitializer.initializeEMF();
		catRepo = CategoryRepository.REPOSITORY;
		prodRepo = ProductRepository.REPOSITORY;
		orderItemRepo = OrderItemRepository.REPOSITORY;
		orderRepo = OrderRepository.REPOSITORY;
		userRepo = UserRepository.REPOSITORY;
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testRepos() {
		//get initial repo sizes
		long categorySize = catRepo.getAllEntities().size();
		long productSize = prodRepo.getAllEntities().size();
		long orderItemSize = orderItemRepo.getAllEntities().size();
		long orderSize = orderRepo.getAllEntities().size();
		long userSize = userRepo.getAllEntities().size();
		
		//create two users
		User createUser = new User();
		createUser.setRealName("Me");
		createUser.setUserName("me");
		createUser.setEmail("me@descartes.tools");
		createUser.setPassword("secret");
		long uId1 = userRepo.createEntity(createUser);
		Assert.assertTrue(uId1 > 0);
		createUser.setRealName("You");
		createUser.setUserName("you");
		createUser.setEmail("you@descartes.tools");
		long uId2 = userRepo.createEntity(createUser);
		Assert.assertTrue(uId2 > 0);
		
		//create two categories
		Category createCat1 = new Category();
		createCat1.setName("Category1");
		createCat1.setDescription("Category Description1");
		long cId1 = catRepo.createEntity(createCat1);
		Assert.assertTrue(cId1 >= 0);
		createCat1.setName("Category2");
		createCat1.setDescription("Category Description2");
		long cId2 = catRepo.createEntity(createCat1);
		Assert.assertTrue(cId2 >= 0);
		
		//list categories
		Assert.assertEquals(catRepo.getAllEntities().size(), 2 + categorySize);
		
		//get and update category
		PersistenceCategory cat1 = catRepo.getEntity(cId1);
		Assert.assertEquals(cat1.getName(), "Category1");
		createCat1.setName("UpdatedCategory1");
		createCat1.setDescription(cat1.getDescription());
		catRepo.updateEntity(cId1, createCat1);
		cat1 = catRepo.getEntity(cId1);
		Assert.assertEquals(cat1.getName(), "UpdatedCategory1");
		
		//Create two products in cat1
		Product product = new Product();
		product.setCategoryId(cId1);
		product.setName("Prod1");
		product.setDescription("pd1");
		product.setListPriceInCents(1);
		long pId1 = prodRepo.createEntity(product);
		product.setName("Prod2");
		product.setDescription("pd2");
		product.setListPriceInCents(2);
		long pId2 = prodRepo.createEntity(product);
		Assert.assertTrue(pId1 > 0);
		//create an invalid product
		product.setCategoryId(-1L);
		long invpId = prodRepo.createEntity(product);
		Assert.assertFalse(invpId > 0);
		
		//get and update product
		PersistenceProduct prod1 = prodRepo.getEntity(pId1);
		Assert.assertEquals(prod1.getName(), "Prod1");
		product.setCategoryId(cId1);
		product.setName("UpdatedProd1");
		product.setDescription(prod1.getDescription());
		product.setListPriceInCents(prod1.getListPriceInCents());
		Assert.assertTrue(prodRepo.updateEntity(pId1, product));
		prod1 = prodRepo.getEntity(pId1);
		Assert.assertEquals(prod1.getName(), "UpdatedProd1");
		Assert.assertEquals(prod1.getDescription(), "pd1");
		
		//get all products
		Assert.assertEquals(prodRepo.getAllEntities().size(), 2 + productSize);
		
		//create three orders
		Order creationOrder = new Order();
		creationOrder.setTotalPriceInCents(500);
		creationOrder.setUserId(uId1);
		long oId1 = orderRepo.createEntity(creationOrder);
		Assert.assertTrue(oId1 > 0);
		creationOrder.setTotalPriceInCents(600);
		creationOrder.setUserId(uId2);
		long oId2 = orderRepo.createEntity(creationOrder);
		Assert.assertTrue(oId2 > 0);
		creationOrder.setTotalPriceInCents(700);
		creationOrder.setUserId(uId2);
		long oId3 = orderRepo.createEntity(creationOrder);
		Assert.assertTrue(oId3 > 0);
		
		//get and update user; get all users
		User user = userRepo.getEntity(uId1);
		user.setRealName("Updated");
		Assert.assertTrue(userRepo.updateEntity(uId1, user));
		user = userRepo.getEntity(uId1);
		Assert.assertEquals("Updated", user.getRealName());
		Assert.assertEquals(2 + userSize, userRepo.getAllEntities().size());
		
		//create five order items
		OrderItem coi = new OrderItem();
		coi.setOrderId(oId1);
		coi.setProductId(pId1);
		coi.setQuantity(5);
		coi.setUnitPriceInCents(5);
		long oiId1 = orderItemRepo.createEntity(coi);
		Assert.assertTrue(oiId1 > 0);
		coi.setQuantity(6);
		Assert.assertTrue(orderItemRepo.createEntity(coi) > 0);
		coi.setProductId(pId2);
		coi.setQuantity(7);
		Assert.assertTrue(orderItemRepo.createEntity(coi) > 0);
		coi.setOrderId(oId2);
		coi.setProductId(pId2);
		coi.setQuantity(8);
		Assert.assertTrue(orderItemRepo.createEntity(coi) > 0);
		coi.setOrderId(oId3);
		coi.setProductId(pId2);
		coi.setQuantity(9);
		long oiId5 = orderItemRepo.createEntity(coi);
		Assert.assertTrue(oiId5 > 0);

		//update and get order item
		coi.setProductId(pId1);
		coi.setQuantity(2);
		Assert.assertTrue(orderItemRepo.updateEntity(oiId1, coi));
		PersistenceOrderItem oi1 = orderItemRepo.getEntity(oiId1);
		Assert.assertEquals(oi1.getQuantity(), 2);
		Assert.assertEquals(oi1.getUnitPriceInCents(), 5);
		
		//delete order 3, should delete order item 5
		Assert.assertTrue(orderRepo.removeEntity(oId3));
		Assert.assertNull(orderItemRepo.getEntity(oiId5));
		//delete user two, should delete order 2 and its order item (order item 4)
		Assert.assertTrue(userRepo.removeEntity(uId2));
		Assert.assertEquals(1 + userSize, userRepo.getAllEntities().size());
		Assert.assertNull(orderRepo.getEntity(oId2));
		
		//delete order item
		Assert.assertTrue(orderItemRepo.removeEntity(oiId1));
		
		//get order items with and without product specification
		Assert.assertEquals(orderItemRepo.getAllEntities().size(), 2 + orderItemSize);
		Assert.assertEquals(orderItemRepo.getAllEntities(1, 1).size(), 1);
		Assert.assertEquals(orderItemRepo.getAllEntitiesWithProduct(pId2, -1, -1).size(), 1);
		
		//get orders and users
		Assert.assertEquals(1 + userSize, userRepo.getAllEntities().size());
		Assert.assertEquals(1 + orderSize, orderRepo.getAllEntities().size());
		//get and update order
		Order order = orderRepo.getEntity(oId1);
		Assert.assertEquals(500, order.getTotalPriceInCents());
		order.setTotalPriceInCents(5);
		orderRepo.updateEntity(oId1, order);
		order = orderRepo.getEntity(oId1);
		Assert.assertEquals(5, order.getTotalPriceInCents());
		
		
		//delete product
		prodRepo.removeEntity(pId1);
		Assert.assertEquals(prodRepo.getAllEntities().size(), 1 + productSize);
		//order item for product should also have been deleted
		Assert.assertEquals(orderItemRepo.getAllEntities().size(), 1 + orderItemSize);
		
		//Get Products with Category
		List<PersistenceProduct> c1p = prodRepo.getAllEntities(cId1, -1, -1);
		Assert.assertEquals(c1p.size(), 1);
		Assert.assertEquals(c1p.get(0).getName(), "Prod2");
		Assert.assertEquals(prodRepo.getAllEntities(cId2, -1, 2).size(), 0);
		
		//delete category with product
		catRepo.removeEntity(cId1);
		Assert.assertEquals(catRepo.getAllEntities().size(), 1 + categorySize);
		//product for category should also have been deleted
		Assert.assertEquals(prodRepo.getAllEntities().size(), productSize);
		//order item for product in category should also have been deleted
		Assert.assertEquals(orderItemRepo.getAllEntities().size(), 0 + orderItemSize);
	}
	
	
}
