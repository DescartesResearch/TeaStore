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
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tools.descartes.teastore.image.StoreImage;

public class TestTimedEntry {

  @Mock
  private StoreImage mockedImg;

  @Before
  public void initialize() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testConstructor() {
    new TimedEntry<>(mockedImg);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorNull() {
    new TimedEntry<StoreImage>(null);
  }

  @Test
  public void testTime() {
    TimedEntry<StoreImage> uut = new TimedEntry<>(mockedImg);
    assertNotEquals(0L, uut.getTime());
    long uutTime = uut.getTime();
    uut.wasUsed();
    assertNotEquals(uutTime, uut.getTime());
    long currentTime = System.nanoTime();
    uut.wasUsed();
    assertEquals(currentTime, uut.getTime(), 5e5);
  }

}
