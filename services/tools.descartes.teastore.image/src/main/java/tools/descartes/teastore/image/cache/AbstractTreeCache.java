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

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;

import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.cache.entry.ICacheEntry;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * Abstract cache class using a sorted tree as internal collection for cache entries.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 * @param <F> Entry Wrapper Type.
 */
public abstract class AbstractTreeCache<T extends ICachable<T>, F extends ICacheEntry<T>>
    extends AbstractCache<TreeSet<F>, T, F> {

  /**
   * Base constructor used by specific implementations.
   * @param cachedStorage Storage object to query if an entry is not found in the cache.
   * @param maxCacheSize Maximum memory used by the cache in bytes.
   * @param cachingRule Caching rule determining if data should be cached.
   * @param ordering Predicate determining the eviction strategy (sorting if the internal tree) if no memory is left. 
   */
  public AbstractTreeCache(IDataStorage<T> cachedStorage, long maxCacheSize,
      Predicate<T> cachingRule, Comparator<F> ordering) {
    super(new TreeSet<>(ordering), cachedStorage, maxCacheSize, cachingRule);
  }

  @Override
  protected abstract F createEntry(T data);

  @Override
  protected void removeEntryByCachingStrategy() {
    dataRemovedFromCache(getEntries().pollFirst().getByteSize());
  }

  @Override
  protected void reorderAndTag(F data) {
    getEntries().remove(data);
    data.wasUsed();
    getEntries().add(data);
  }

}
