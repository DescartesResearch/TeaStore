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
    uut = new ImageDBKey(TEST_FILENAME);
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
