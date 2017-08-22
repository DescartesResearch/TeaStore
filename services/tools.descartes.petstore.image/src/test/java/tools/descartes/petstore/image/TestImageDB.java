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
	private final long nameImageID = 200;
	private final int width = 123;
	private final int height= 321;
	private final int widhtLarge = 1230;
	private final int heightLarge = 3210;
	
	@Mock
	private ImageDBKey mockedProductKey;
	@Mock
	private ImageDBKey mockedNameKey;
	@Mock
	private ImageSize mockedSize;
	@Mock
	private ImageSize mockedSizeLarge;
	
	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		when(mockedProductKey.isProductKey()).thenReturn(true);
		when(mockedProductKey.getProductID()).thenReturn(productID);
		when(mockedProductKey.getWebUIName()).thenReturn(null);
		when(mockedNameKey.isProductKey()).thenReturn(false);
		when(mockedNameKey.getProductID()).thenReturn(0L);
		when(mockedNameKey.getWebUIName()).thenReturn(name);
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
		ImageDB uut = new ImageDB();
		uut.setImageMapping(mockedProductKey, productImageID, mockedSize);
		assertTrue(uut.hasImageID(mockedProductKey, mockedSize));
		assertFalse(uut.hasImageID(mockedNameKey, mockedSize));
		
		uut = new ImageDB();
		uut.setImageMapping(mockedNameKey, nameImageID, mockedSize);
		assertTrue(uut.hasImageID(mockedNameKey, mockedSize));
		assertFalse(uut.hasImageID(mockedProductKey, mockedSize));
	}
	
	@Test(expected = NullPointerException.class)
	public void testHasImageIDNull() {
		ImageDB uut = new ImageDB();
		uut.hasImageID((ImageDBKey)null, mockedSize);
	}
	
	@Test
	public void testGetImageID() {
		ImageDB uut = new ImageDB();
		assertEquals(0, uut.getImageID(mockedProductKey, mockedSize));
		assertEquals(0, uut.getImageID(mockedProductKey, mockedSize));
		
		uut = new ImageDB();
		uut.setImageMapping(mockedProductKey, productImageID, mockedSize);
		assertEquals(productImageID, uut.getImageID(mockedProductKey, mockedSize));
		assertEquals(0, uut.getImageID(mockedNameKey, mockedSize));
		assertNotEquals(nameImageID, uut.getImageID(mockedProductKey, mockedSize));
		assertNotEquals(0, uut.getImageID(mockedProductKey, mockedSize));
		
		
		uut.setImageMapping(mockedProductKey, productImageID, mockedSize);
		uut.setImageMapping(mockedNameKey, nameImageID, mockedSize);
		assertEquals(productImageID, uut.getImageID(mockedProductKey, mockedSize));
		assertNotEquals(nameImageID, uut.getImageID(mockedProductKey, mockedSize));
		assertNotEquals(0, uut.getImageID(mockedProductKey, mockedSize));
	}
	
}
