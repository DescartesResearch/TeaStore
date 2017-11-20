package tools.descartes.petsupplystore.store.rest;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.descartes.petsupplystore.entities.Category;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Tests for the user information retrieval.
 * @author Simon
 *
 */
public class CategoryInformationTest extends AbstractStoreRestTest {

	/**
	 * Tests for the queries about user information.
	 * @throws JsonProcessingException 
	 */
	@Test
	public void runTest() throws JsonProcessingException {
		mockCategory3();
		mockGetCategories();
		
		Category category = LoadBalancedStoreOperations.getCategory(3);
		Assert.assertTrue(category != null);
		
		try {
			LoadBalancedStoreOperations.getCategory(-1);
			Assert.fail();
		} catch (Exception e) {}
		
		List<Category> categories = LoadBalancedStoreOperations.getCategories();
		Assert.assertTrue(categories.size() != 0);
	}
	
	private void mockGetCategories() {
		Category c = new Category();
		c.setDescription("This is a category");
		c.setId(3);
		c.setName("category");
		List<Category> cs = new LinkedList<Category>();
		cs.add(c);
		mockValidGetRestCall(cs, "/tools.descartes.petsupplystore.persistence/rest/categories");
	}

	private void mockCategory3() {
		Category c = new Category();
		c.setDescription("This is a category");
		c.setId(3);
		c.setName("category");
		mockValidGetRestCall(c, "/tools.descartes.petsupplystore.persistence/rest/categories/3");
	}
}
