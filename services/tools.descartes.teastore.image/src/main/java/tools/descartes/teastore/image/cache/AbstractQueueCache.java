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
package tools.descartes.teastore.image.cache;

import java.util.LinkedList;
import java.util.function.Predicate;

import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.cache.entry.SimpleEntry;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * Abstract cache class using a queue as internal collection for cache entries.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
public abstract class AbstractQueueCache<T extends ICachable<T>>
    extends AbstractCache<LinkedList<SimpleEntry<T>>, T, SimpleEntry<T>> {

  /**
   * Base constructor used by specific implementations.
   * @param cachedStorage Storage object to query if an entry is not found in the cache.
   * @param maxCacheSize Maximum memory used by the cache in bytes.
   * @param cachingRule Caching rule determining if data should be cached.
   */
  public AbstractQueueCache(IDataStorage<T> cachedStorage, long maxCacheSize,
      Predicate<T> cachingRule) {
    super(new LinkedList<>(), cachedStorage, maxCacheSize, cachingRule);
  }

  /*
   * Implementations of abstract superclass
   */

  @Override
  public SimpleEntry<T> createEntry(T data) {
    return new SimpleEntry<T>(data);
  }

  @Override
  protected abstract void removeEntryByCachingStrategy();

}
