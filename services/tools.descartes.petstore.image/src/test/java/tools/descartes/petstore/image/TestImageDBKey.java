package tools.descartes.petstore.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestImageDBKey {

	private static final long TEST_PRODUCT_KEY = 120;
	private static final String TEST_FILENAME = "testfilename";
	
	@Test
	public void testConstructor() {
		new ImageDBKey(TEST_PRODUCT_KEY);
		new ImageDBKey(TEST_FILENAME);
	}
	
	@Test
	public void testIsProductKey() {
		ImageDBKey uut = new ImageDBKey(TEST_PRODUCT_KEY);
		assertTrue(uut.isProductKey());
		uut = new  ImageDBKey(TEST_FILENAME);
		assertFalse(uut.isProductKey());
	}
	
	@Test
	public void testGetProductID() {
		ImageDBKey uut = new ImageDBKey(TEST_PRODUCT_KEY);
		assertEquals(TEST_PRODUCT_KEY, uut.getProductID());
		assertNull(uut.getWebUIName());
	}
	
	@Test
	public void testGetWebUIName() {
		ImageDBKey uut = new ImageDBKey(TEST_FILENAME);
		assertEquals(TEST_FILENAME, uut.getWebUIName());
		assertEquals(0, uut.getProductID());
	}
}
