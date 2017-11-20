package tools.descartes.petsupplystore.store.rest;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.descartes.petsupplystore.entities.Product;
import tools.descartes.petsupplystore.registryclient.rest.LoadBalancedStoreOperations;



/**
 * Tests for the user information retrieval.
 * @author Simon
 *
 */
public class ProductInformationTest extends AbstractStoreRestTest {

	/**
	 * Tests for the queries about user information.
	 * @throws JsonProcessingException 
	 */
	@Test
	public void runTest() throws JsonProcessingException {
		mockProduct106();
		mockProducts();
		mockNumberOfProducts();
		
		Product  product = LoadBalancedStoreOperations.getProduct(106);
		Assert.assertTrue(product != null);
		
		try {
			LoadBalancedStoreOperations.getProduct(-1);
			Assert.fail();
		} catch (Exception e) {}
		
		List<Product> products = LoadBalancedStoreOperations.getProducts(3, 1, 10);
		Assert.assertEquals(10, products.size());
		
		List<Product> products2 = LoadBalancedStoreOperations.getProducts(3, 2, 10);
		Assert.assertEquals(10, products2.size());
		
		List<Product> products3 = LoadBalancedStoreOperations.getProducts(3, 1, 20);
		Assert.assertEquals(10, products2.size());

		for (Product p: products) {
			Assert.assertTrue(products3.contains(p));
		}
		for (Product p2: products2) {
			Assert.assertTrue(products3.contains(p2));
		}
		
		int numberOfProducts = LoadBalancedStoreOperations.getNumberOfProducts(3);
		Assert.assertEquals(100, numberOfProducts);
	}

	private void mockNumberOfProducts() {
		mockValidGetRestCall(100, "/tools.descartes.petsupplystore.persistence/rest/products/count/3");
	}
	
	private void mockProducts() {
		Product p = new Product();
		p.setDescription("This is a product");
		p.setCategoryId(3);
		p.setId(15);
		p.setListPriceInCents(99);
		p.setName("product");
		List<Product> ps = new LinkedList<Product>();
		for (int i = 0; i < 10; i++)
			ps.add(p);
		mockValidGetRestCall(ps, "/tools.descartes.petsupplystore.persistence/rest/products/category/3?start=0&max=10");
		mockValidGetRestCall(ps, "/tools.descartes.petsupplystore.persistence/rest/products/category/3?start=10&max=10");
		for (int i = 0; i < 10; i++)
			ps.add(p);
		mockValidGetRestCall(ps, "/tools.descartes.petsupplystore.persistence/rest/products/category/3?start=0&max=20");
		
		
	}
}
