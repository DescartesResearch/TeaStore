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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.descartes.teastore.persistence.domain.CategoryRepository;
import tools.descartes.teastore.persistence.domain.OrderItemRepository;
import tools.descartes.teastore.persistence.domain.OrderRepository;
import tools.descartes.teastore.persistence.domain.PersistenceCategory;
import tools.descartes.teastore.persistence.domain.ProductRepository;
import tools.descartes.teastore.persistence.domain.UserRepository;
import tools.descartes.teastore.persistence.repository.DataGenerator;
import tools.descartes.teastore.persistence.repository.EMFManagerInitializer;

/**
 * Test for the DataGenerator.
 * @author Joakim von Kistowski
 *
 */
public class DataGeneratorTest {
	
	private static final int CATEGORIES = 15;
	private static final int PRODUCTS = 30;
	private static final int USERS = 20;
	private static final int MAX_ORDERS = 10;

	/**
	 * Setup the test.
	 * @throws Throwable On failure.
	 */
	@Before
	public void setup() throws Throwable {
		EMFManagerInitializer.initializeEMF();
	}
	
	/**
	 * Run the test.
	 */
	@Test
	public void testRepos() {
		//get initial repo sizes
		int initialCategories = CategoryRepository.REPOSITORY.getAllEntities().size();
		int initialUsers = UserRepository.REPOSITORY.getAllEntities().size();
		
		//generate data
		DataGenerator.GENERATOR.generateDatabaseContent(CATEGORIES, PRODUCTS, USERS, MAX_ORDERS);
		
		//assertions
		Assert.assertEquals(CATEGORIES + initialCategories, CategoryRepository.REPOSITORY.getAllEntities().size());
		CategoryRepository.REPOSITORY.getAllEntities().forEach(c ->
			Assert.assertTrue(c.getName() != null && !c.getName().isEmpty()));
		for (PersistenceCategory category : CategoryRepository.REPOSITORY.getAllEntities()) {
			Assert.assertEquals(PRODUCTS, category.getProducts().size());
			category.getProducts().forEach(p -> Assert.assertTrue(p.getName() != null && !p.getName().isEmpty()));
		}
		Assert.assertEquals(USERS + initialUsers, UserRepository.REPOSITORY.getAllEntities().size());
		Pattern userPattern = Pattern.compile("user\\d+");
		UserRepository.REPOSITORY.getAllEntities().forEach(u ->
			Assert.assertTrue(userPattern.matcher(u.getUserName()).matches()));
		Assert.assertTrue(OrderRepository.REPOSITORY.getAllEntities().size() > 0);
		Assert.assertTrue(UserRepository.REPOSITORY.getAllEntities().stream().anyMatch(u -> u.getOrders().size() > 0));
		OrderRepository.REPOSITORY.getAllEntities().forEach(o -> Assert.assertTrue(o.getOrderItems().size() > 0));
		
		//Re-create database
		DataGenerator.GENERATOR.dropAndCreateTables();
		DataGenerator.GENERATOR.generateDatabaseContent(2, 2, 2, 0);
		Assert.assertEquals(2, CategoryRepository.REPOSITORY.getAllEntities().size());
		Assert.assertEquals(2 * 2, ProductRepository.REPOSITORY.getAllEntities().size());
		Assert.assertEquals(2, UserRepository.REPOSITORY.getAllEntities().size());
		Assert.assertTrue(OrderRepository.REPOSITORY.getAllEntities().isEmpty());
		Assert.assertTrue(OrderItemRepository.REPOSITORY.getAllEntities().isEmpty());
	}
	
}
