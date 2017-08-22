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
package tools.descartes.petstore.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.petstore.entities.ImageSize;

public class TestImageDB {

	private final long productID = 1234567890;
	private final String name = "testname";
	private final long productImageID = 99;
	private final long productImageIDLarge = 999;
	private final long nameImageID = 200;
	private ImageSize size = ImageSize.ICON;
	private ImageSize sizeLarge = ImageSize.FULL;
	private ImageDB uut;
	
	@Mock
	private ImageDBKey mockedProductKey;
	@Mock
	private ImageDBKey mockedNameKey;

	
	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		when(mockedProductKey.isProductKey()).thenReturn(true);
		when(mockedProductKey.getProductID()).thenReturn(productID);
		when(mockedProductKey.getWebUIName()).thenReturn(null);
		when(mockedNameKey.isProductKey()).thenReturn(false);
		when(mockedNameKey.getProductID()).thenReturn(0L);
		when(mockedNameKey.getWebUIName()).thenReturn(name);
		uut = new ImageDB();
	}
	
	@Test
	public void testConstructor() {
		ImageDB uut = new ImageDB();
		new ImageDB(uut);
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructorNull() {
		new ImageDB(null);
	}
	
	@Test
	public void testHasImageID() {
		uut.setImageMapping(mockedProductKey, productImageID, size);
		assertTrue(uut.hasImageID(mockedProductKey, size));
		assertFalse(uut.hasImageID(mockedNameKey, size));
		
		uut = new ImageDB();
		uut.setImageMapping(mockedNameKey, nameImageID, size);
		assertTrue(uut.hasImageID(mockedNameKey, size));
		assertFalse(uut.hasImageID(mockedProductKey, size));
	}
	
	@Test(expected = NullPointerException.class)
	public void testHasImageIDNull() {
		uut.hasImageID((ImageDBKey)null, size);
	}
	
	@Test
	public void testSetImageMappingAndGetImageID() {
		assertEquals(0, uut.getImageID(mockedProductKey, size));
		assertEquals(0, uut.getImageID(mockedProductKey, size));
		assertEquals(0, uut.getImageID(mockedProductKey, null));
		
		uut = new ImageDB();
		uut.setImageMapping(mockedProductKey, productImageID, size);
		assertEquals(0, uut.getImageID(mockedProductKey, null));	
		assertEquals(productImageID, uut.getImageID(mockedProductKey, size));
		assertNotEquals(nameImageID, uut.getImageID(mockedProductKey, size));
		assertNotEquals(0, uut.getImageID(mockedProductKey, size));
		
		assertEquals(0, uut.getImageID(mockedNameKey, null));
		assertEquals(0, uut.getImageID(mockedNameKey, size));
		assertNotEquals(productImageID, uut.getImageID(mockedNameKey, size));

		uut = new ImageDB();
		uut.setImageMapping(mockedProductKey, productImageID, size);
		uut.setImageMapping(mockedNameKey, nameImageID, size);
		assertEquals(productImageID, uut.getImageID(mockedProductKey, size));
		assertNotEquals(nameImageID, uut.getImageID(mockedProductKey, size));
		assertNotEquals(0, uut.getImageID(mockedProductKey, size));
		
		assertEquals(nameImageID, uut.getImageID(mockedNameKey, size));
		assertNotEquals(productImageID, uut.getImageID(mockedNameKey, size));
		assertNotEquals(0, uut.getImageID(mockedNameKey, size));

		uut.setImageMapping(mockedProductKey, productImageIDLarge, sizeLarge);
		assertEquals(0, uut.getImageID(mockedProductKey, null));
		assertEquals(productImageID, uut.getImageID(mockedProductKey, size));
		assertNotEquals(productImageIDLarge, uut.getImageID(mockedProductKey, size));
		assertNotEquals(0, uut.getImageID(mockedProductKey, size));
		assertNotEquals(nameImageID, uut.getImageID(mockedProductKey, size));
		
		assertEquals(0, uut.getImageID(mockedNameKey, null));
		assertEquals(0, uut.getImageID(mockedNameKey, sizeLarge));	
		assertNotEquals(productImageIDLarge, uut.getImageID(mockedNameKey, sizeLarge));
		assertNotEquals(productImageID, uut.getImageID(mockedNameKey, sizeLarge));
		assertNotEquals(nameImageID, uut.getImageID(mockedNameKey, sizeLarge));
	
		assertEquals(nameImageID, uut.getImageID(mockedNameKey, size));
		assertNotEquals(0, uut.getImageID(mockedNameKey, size));
		assertNotEquals(productImageID, uut.getImageID(mockedNameKey, size));
		assertNotEquals(productImageIDLarge, uut.getImageID(mockedNameKey, size));
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetImageIDNull() {
		uut.setImageMapping(mockedProductKey, productImageID, size);
		uut.setImageMapping(mockedNameKey, nameImageID, size);
		
		uut.getImageID((ImageDBKey)null, size);
	}
	
	@Test
	public void testGetImageSize() {
		assertNull(uut.getImageSize(productImageID));
		
		uut.setImageMapping(mockedProductKey, productImageID, size);
		assertNull(uut.getImageSize(nameImageID));
		assertEquals(size, uut.getImageSize(productImageID));
		
		uut.setImageMapping(mockedProductKey, productImageIDLarge, sizeLarge);
		assertNull(uut.getImageSize(nameImageID));
		assertNotEquals(sizeLarge, uut.getImageSize(productImageID));
		assertEquals(size, uut.getImageSize(productImageID));
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetImageMappingKeyNull() {
		uut.setImageMapping((ImageDBKey)null, nameImageID, size);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetImageMappingNameNull() {
		uut.setImageMapping((String)null, nameImageID, size);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetImageMappingProductSizeNull() {
		uut.setImageMapping(productID, productImageID, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetImageMappingNameSizeNull() {
		uut.setImageMapping(name, nameImageID, null);	
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetImageMappingKeySizeNull() {
		uut.setImageMapping(mockedNameKey, nameImageID, null);
	}
}
