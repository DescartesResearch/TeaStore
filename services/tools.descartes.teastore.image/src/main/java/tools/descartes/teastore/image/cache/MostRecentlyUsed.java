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

import java.util.function.Predicate;

import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.cache.entry.TimedEntry;
import tools.descartes.teastore.image.cache.rules.CacheAll;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * Most recently used (MRU) cache implementation.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
public class MostRecentlyUsed<T extends ICachable<T>> extends AbstractTreeCache<T, TimedEntry<T>> {

  /**
   * MRU cache standard constructor setting the maximum cache size to the standard value 
   * {@link tools.descartes.teastore.image.cache.IDataCache.STD_MAX_CACHE_SIZE} and allowing all data to be cached.
   */
  public MostRecentlyUsed() {
    this(IDataCache.STD_MAX_CACHE_SIZE);
  }
	
  /**
   * MRU cache constructor setting the maximum cache size to the given size and allowing all data to be cached.
   * @param maxCacheSize Maximum cache size in bytes.
   */
  public MostRecentlyUsed(long maxCacheSize) {
    this(maxCacheSize, new CacheAll<T>());
  }

  /**
   * MRU cache constructor setting the maximum cache size to the given size and caching only data that is tested true 
   * for the given caching rule.
   * @param maxCacheSize Maximum cache size in bytes.
   * @param cachingRule Cache rule determining which data will be cached.
   */
  public MostRecentlyUsed(long maxCacheSize, Predicate<T> cachingRule) {
    this(null, maxCacheSize, cachingRule);
  }

  /**
   * MRU cache constructor setting the maximum cache size to the given size and caching only data that is tested true 
   * for the given caching rule. This constructor also lets you set the underlying storage, queried if an entry is not 
   * found in the cache.
   * @param cachedStorage Storage object to query if an entry is not found in the cache.
   * @param maxCacheSize Maximum cache size in bytes.
   * @param cachingRule Cache rule determining which data will be cached.
   */
  public MostRecentlyUsed(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
    super(cachedStorage, maxCacheSize, cachingRule, (a, b) -> {
      if (a.getTime() - b.getTime() < 0) {
    	return 1;
      } else if (a.getTime() - b.getTime() > 0) {
    	return -1;
      } else if (a.getId() < b.getId()) {
    	return -1;
      } else if (a.getId() == b.getId()) {
    	return 0;
      } else {
    	return -1;
      }
    });
  }

  @Override
  protected TimedEntry<T> createEntry(T data) {
    return new TimedEntry<>(data);
  }

}
