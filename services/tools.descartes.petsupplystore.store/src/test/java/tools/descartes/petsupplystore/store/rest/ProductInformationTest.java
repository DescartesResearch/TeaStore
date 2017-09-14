package tools.descartes.petsupplystore.store.rest;

import java.util.List;

import org.junit.Assert;

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
	 */
	protected void runTest() {
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
		
		for (Product p: products) {
			Assert.assertFalse(products2.contains(p));
		}
		
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
}
