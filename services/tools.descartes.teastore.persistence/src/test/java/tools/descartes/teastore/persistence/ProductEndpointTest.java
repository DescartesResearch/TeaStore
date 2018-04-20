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

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import tools.descartes.teastore.persistence.rest.CategoryEndpoint;
import tools.descartes.teastore.persistence.rest.ProductEndpoint;
import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.Product;

/**
 * Test for the ProductEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class ProductEndpointTest {
	
	private TomcatTestHandler handler;
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		handler = new TomcatTestHandler(ProductEndpoint.class, CategoryEndpoint.class);
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {
		//create category, replace with category endpoint once it is there
		RESTClient<Category> categoryClient = new RESTClient<Category>("http://localhost:"
		+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "categories", Category.class);
		Category cat = new Category();
		cat.setName("Category");
		cat.setDescription("Category Description");
		long categoryId = NonBalancedCRUDOperations.sendEntityForCreation(categoryClient, cat);
		
		//open connection
		RESTClient<Product> client = new RESTClient<Product>("http://localhost:"
		+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "products", Product.class);
		//get initial product table size
		int initialProducts = NonBalancedCRUDOperations.getEntities(client, -1, -1).size();
		
		
		//create product
		Product creationProduct = new Product();
		creationProduct.setCategoryId(categoryId);
		creationProduct.setName("rest product");
		creationProduct.setDescription("rest description");
		creationProduct.setListPriceInCents(999);
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		Assert.assertTrue(id >= 0);
		
		//create invalid product
		creationProduct.setCategoryId(-2L);
		long noid = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		Assert.assertTrue(noid <= 0);
		
		//update product
		creationProduct.setName("updated");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, creationProduct));
		try {
			NonBalancedCRUDOperations.sendEntityForUpdate(client, -1L, creationProduct);
			Assert.fail();
		} catch (NotFoundException e) {
			//don't fail
		}
		
		//receive product
		Product recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getName(), "updated");
		
		//create 2 additional products
		creationProduct.setCategoryId(categoryId);
		creationProduct.setName("test2");
		long id2 = NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		creationProduct.setName("test3");
		NonBalancedCRUDOperations.sendEntityForCreation(client, creationProduct);
		
		//get some products
		List<Product> products = NonBalancedCRUDOperations.getEntities(client, 1, 2);
		Assert.assertEquals(products.size(), 2);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 3 + initialProducts);
		
		//get products with category
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, "category", categoryId, -1, -1).size(), 3);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, "category", 99999, -1, -1).size(), 0);
		Assert.assertEquals("3", client.getEndpointTarget().path("count").path("" + categoryId)
				.request(MediaType.APPLICATION_JSON).get().readEntity(String.class));
		Assert.assertEquals(404, client.getEndpointTarget().path("count").path("99999")
				.request(MediaType.APPLICATION_JSON).get().getStatus());
		
		//delete product
		NonBalancedCRUDOperations.deleteEntity(client, id2);
		products = NonBalancedCRUDOperations.getEntities(client, -1, -1);
		Assert.assertTrue(products.size() >= 2 && products.stream().noneMatch(te -> te.getId() == id2));
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
