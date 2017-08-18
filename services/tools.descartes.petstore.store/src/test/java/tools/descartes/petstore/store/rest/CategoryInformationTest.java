package tools.descartes.petstore.store.rest;

import java.util.List;

import org.junit.Assert;

import tools.descartes.petstore.entities.Category;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Tests for the user information retrieval.
 * @author Simon
 *
 */
public class CategoryInformationTest extends AbstractStoreRestTest {

	/**
	 * Tests for the queries about user information.
	 */
	protected void runTest() {
		Category category = LoadBalancedStoreOperations.getCategory(3);
		Assert.assertTrue(category != null);
		
		Category category2 = LoadBalancedStoreOperations.getCategory(-1);
		Assert.assertTrue(category2 == null);
		
		List<Category> categories = LoadBalancedStoreOperations.getCategories();
		Assert.assertTrue(categories.size() != 0);
	}
}
