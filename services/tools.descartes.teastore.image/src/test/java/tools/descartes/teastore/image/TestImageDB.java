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
package tools.descartes.teastore.image;

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

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.entities.ImageSizePreset;

public class TestImageDB {

  private static final long PRODUCT_ID = 1234567890;
  private static final String NAME = "testname";
  private static final long PRODUCT_IMAGE_ID = 99;
  private static final long PRODUCT_IMAGE_ID_LARGE = 999;
  private static final long NAME_IMAGE_ID = 200;
  private ImageSize size = ImageSizePreset.ICON.getSize();
  private ImageSize sizeLarge = ImageSizePreset.FULL.getSize();
  private ImageDB uut;

  @Mock
  private ImageDBKey mockedProductKey;
  @Mock
  private ImageDBKey mockedNameKey;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
    when(mockedProductKey.isProductKey()).thenReturn(true);
    when(mockedProductKey.getProductID()).thenReturn(PRODUCT_ID);
    when(mockedProductKey.getWebUIName()).thenReturn(null);
    when(mockedNameKey.isProductKey()).thenReturn(false);
    when(mockedNameKey.getProductID()).thenReturn(0L);
    when(mockedNameKey.getWebUIName()).thenReturn(NAME);
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
    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID, size);
    assertTrue(uut.hasImageID(mockedProductKey, size));
    assertFalse(uut.hasImageID(mockedNameKey, size));

    uut = new ImageDB();
    uut.setImageMapping(mockedNameKey, NAME_IMAGE_ID, size);
    assertTrue(uut.hasImageID(mockedNameKey, size));
    assertFalse(uut.hasImageID(mockedProductKey, size));
  }

  @Test(expected = NullPointerException.class)
  public void testHasImageIDNull() {
    uut.hasImageID((ImageDBKey) null, size);
  }

  @Test
  public void testSetImageMappingAndGetImageID() {
    assertEquals(0, uut.getImageID(mockedProductKey, size));
    assertEquals(0, uut.getImageID(mockedProductKey, size));
    assertEquals(0, uut.getImageID(mockedProductKey, null));

    uut = new ImageDB();
    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID, size);
    assertEquals(0, uut.getImageID(mockedProductKey, null));
    assertEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedProductKey, size));
    assertNotEquals(NAME_IMAGE_ID, uut.getImageID(mockedProductKey, size));
    assertNotEquals(0, uut.getImageID(mockedProductKey, size));

    assertEquals(0, uut.getImageID(mockedNameKey, null));
    assertEquals(0, uut.getImageID(mockedNameKey, size));
    assertNotEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedNameKey, size));

    uut = new ImageDB();
    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID, size);
    uut.setImageMapping(mockedNameKey, NAME_IMAGE_ID, size);
    assertEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedProductKey, size));
    assertNotEquals(NAME_IMAGE_ID, uut.getImageID(mockedProductKey, size));
    assertNotEquals(0, uut.getImageID(mockedProductKey, size));

    assertEquals(NAME_IMAGE_ID, uut.getImageID(mockedNameKey, size));
    assertNotEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedNameKey, size));
    assertNotEquals(0, uut.getImageID(mockedNameKey, size));

    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID_LARGE, sizeLarge);
    assertEquals(0, uut.getImageID(mockedProductKey, null));
    assertEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedProductKey, size));
    assertNotEquals(PRODUCT_IMAGE_ID_LARGE, uut.getImageID(mockedProductKey, size));
    assertNotEquals(0, uut.getImageID(mockedProductKey, size));
    assertNotEquals(NAME_IMAGE_ID, uut.getImageID(mockedProductKey, size));

    assertEquals(0, uut.getImageID(mockedNameKey, null));
    assertEquals(0, uut.getImageID(mockedNameKey, sizeLarge));
    assertNotEquals(PRODUCT_IMAGE_ID_LARGE, uut.getImageID(mockedNameKey, sizeLarge));
    assertNotEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedNameKey, sizeLarge));
    assertNotEquals(NAME_IMAGE_ID, uut.getImageID(mockedNameKey, sizeLarge));

    assertEquals(NAME_IMAGE_ID, uut.getImageID(mockedNameKey, size));
    assertNotEquals(0, uut.getImageID(mockedNameKey, size));
    assertNotEquals(PRODUCT_IMAGE_ID, uut.getImageID(mockedNameKey, size));
    assertNotEquals(PRODUCT_IMAGE_ID_LARGE, uut.getImageID(mockedNameKey, size));
  }

  @Test(expected = NullPointerException.class)
  public void testGetImageIDNull() {
    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID, size);
    uut.setImageMapping(mockedNameKey, NAME_IMAGE_ID, size);

    uut.getImageID((ImageDBKey) null, size);
  }

  @Test
  public void testGetImageSize() {
    assertNull(uut.getImageSize(PRODUCT_IMAGE_ID));

    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID, size);
    assertNull(uut.getImageSize(NAME_IMAGE_ID));
    assertEquals(size, uut.getImageSize(PRODUCT_IMAGE_ID));

    uut.setImageMapping(mockedProductKey, PRODUCT_IMAGE_ID_LARGE, sizeLarge);
    assertNull(uut.getImageSize(NAME_IMAGE_ID));
    assertNotEquals(sizeLarge, uut.getImageSize(PRODUCT_IMAGE_ID));
    assertEquals(size, uut.getImageSize(PRODUCT_IMAGE_ID));
  }

  @Test(expected = NullPointerException.class)
  public void testSetImageMappingKeyNull() {
    uut.setImageMapping((ImageDBKey) null, NAME_IMAGE_ID, size);
  }

  @Test(expected = NullPointerException.class)
  public void testSetImageMappingNameNull() {
    uut.setImageMapping((String) null, NAME_IMAGE_ID, size);
  }

  @Test(expected = NullPointerException.class)
  public void testSetImageMappingProductSizeNull() {
    uut.setImageMapping(PRODUCT_ID, PRODUCT_IMAGE_ID, null);
  }

  @Test(expected = NullPointerException.class)
  public void testSetImageMappingNameSizeNull() {
    uut.setImageMapping(NAME, NAME_IMAGE_ID, null);
  }

  @Test(expected = NullPointerException.class)
  public void testSetImageMappingKeySizeNull() {
    uut.setImageMapping(mockedNameKey, NAME_IMAGE_ID, null);
  }
}
