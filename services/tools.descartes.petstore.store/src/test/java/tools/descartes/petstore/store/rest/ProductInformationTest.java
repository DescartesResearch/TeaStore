package tools.descartes.petstore.store.rest;

import java.util.List;

import org.junit.Assert;

import tools.descartes.petstore.entities.Order;
import tools.descartes.petstore.entities.Product;
import tools.descartes.petstore.entities.message.SessionBlob;
import tools.descartes.petstore.registryclient.rest.LoadBalancedStoreOperations;



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
		
		Product product2 = LoadBalancedStoreOperations.getProduct(-1);
		Assert.assertTrue(product2 == null);
		
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
		
		List<Product> ads = LoadBalancedStoreOperations.getAdvertisements(new SessionBlob(), 3);
		Assert.assertEquals(3, ads.size());
		
		List<Product> ads2 = LoadBalancedStoreOperations.getAdvertisements(new SessionBlob(), 1);
		Assert.assertEquals(1, ads2.size());
	}
}
