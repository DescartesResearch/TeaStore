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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapper class for caches with a replacement strategy that relies on a
 * timestamp when the entry was retrieved from cache. For example the
 * {@link tools.descartes.teastore.image.cache.LeastRecentlyUsed} or
 * {@link tools.descartes.teastore.image.cache.MostRecentlyUsed}.
 * 
 * @author Norbert Schmitt
 *
 * @param <D>
 *          Cachable data that must implement
 *          {@link tools.descartes.teastore.image.cache.entry.ICachable}
 */
public class TimedEntry<D extends ICachable<D>> extends AbstractEntry<D> {

  private AtomicLong time = new AtomicLong();

  /**
   * Basic constructor storing the cachable data. If the cachable data supplied is
   * null, a {@link java.lang.NullPointerException} is thrown.
   * 
   * @param data
   *          Cachable data
   */
  public TimedEntry(D data) {
    super(data);
    wasUsed();
  }

  /**
   * Returns the last time this entry was retrieved from cache.
   * 
   * @return The last time in nanoseconds
   */
  public long getTime() {
    return time.get();
  }

  @Override
  public void wasUsed() {
    time.set(System.nanoTime());
  }

}
