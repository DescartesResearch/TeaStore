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
package tools.descartes.teastore.image.cache.entry;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.StoreImage;

public class TestCountedEntry {

  @Mock
  private StoreImage mockedImg;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testConstructor() {
    new CountedEntry<>(mockedImg);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorNull() {
    new CountedEntry<StoreImage>(null);
  }

  @Test
  public void testUseCount() {
    CountedEntry<StoreImage> uut = new CountedEntry<>(mockedImg);
    assertEquals(0, uut.getUseCount());
    uut.wasUsed();
    assertEquals(1, uut.getUseCount());
    uut.wasUsed();
    uut.wasUsed();
    assertEquals(3, uut.getUseCount());
  }

}
