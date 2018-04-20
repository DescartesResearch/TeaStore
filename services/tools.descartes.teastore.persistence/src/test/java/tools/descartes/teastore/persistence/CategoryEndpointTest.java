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
import tools.descartes.teastore.registryclient.rest.NonBalancedCRUDOperations;
import tools.descartes.teastore.registryclient.util.NotFoundException;
import tools.descartes.teastore.registryclient.util.RESTClient;
import tools.descartes.teastore.entities.Category;

/**
 * Test for the CategoryEndpoint.
 * @author Joakim von Kistowski
 *
 */
public class CategoryEndpointTest {
	
	private TomcatTestHandler handler;
	
	/**
	 * Setup the test by deploying an embedded tomcat and adding the rest endpoints.
	 * @throws Throwable Throws uncaught throwables for test to fail.
	 */
	@Before
	public void setup() throws Throwable {
		handler = new TomcatTestHandler(CategoryEndpoint.class);
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testEndpoint() {	
		//open connection
		RESTClient<Category> client = new RESTClient<Category>("http://localhost:"
				+ handler.getTomcatPort() + TomcatTestHandler.CONTEXT + "/", "rest", "categories", Category.class);
		int initialSize = NonBalancedCRUDOperations.getEntities(client, -1, -1).size();
		
		//create category
		Category cat = new Category();
		cat.setName("Category");
		cat.setDescription("Category Description");
		long id = NonBalancedCRUDOperations.sendEntityForCreation(client, cat);
		Assert.assertTrue(id >= 0);	
		
		//update category
		cat.setName("updatedCategory");
		Assert.assertTrue(NonBalancedCRUDOperations.sendEntityForUpdate(client, id, cat));
		try {
			NonBalancedCRUDOperations.sendEntityForUpdate(client, 500L, cat);
			Assert.fail();
		} catch (NotFoundException e) {
			//don't fail
		}
		
		//receive category
		Category recEnt = NonBalancedCRUDOperations.getEntity(client, id);
		Assert.assertNotNull(recEnt);
		Assert.assertEquals(recEnt.getName(), "updatedCategory");
		
		//create 1 additional category 
		cat.setName("test2");
		NonBalancedCRUDOperations.sendEntityForCreation(client, cat);
		
		//get some categories
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, 1, 1).size(), 1);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 2 + initialSize);
		
		//delete category
		NonBalancedCRUDOperations.deleteEntity(client, id);
		Assert.assertEquals(NonBalancedCRUDOperations.getEntities(client, -1, -1).size(), 1 + initialSize);
		Assert.assertTrue(NonBalancedCRUDOperations.getEntities(client, -1, -1)
				.stream().noneMatch(te -> te.getId() == id));
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
