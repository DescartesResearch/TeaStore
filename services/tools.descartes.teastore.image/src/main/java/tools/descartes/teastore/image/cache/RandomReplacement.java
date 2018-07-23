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

import java.util.Random;
import java.util.function.Predicate;

import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.cache.rules.CacheAll;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * Random replacement (RR) cache implementation.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
public class RandomReplacement<T extends ICachable<T>> extends AbstractQueueCache<T> {

  private Random rand = new Random();

  /**
   * RR cache standard constructor setting the maximum cache size to the standard value 
   * {@link tools.descartes.teastore.image.cache.IDataCache.STD_MAX_CACHE_SIZE} and allowing all data to be cached.
   */
  public RandomReplacement() {
    this(IDataCache.STD_MAX_CACHE_SIZE);
  }

  /**
   * RR cache constructor setting the maximum cache size to the given size and allowing all data to be cached.
   * @param maxCacheSize Maximum cache size in bytes.
   */
  public RandomReplacement(long maxCacheSize) {
    this(maxCacheSize, new CacheAll<T>());
  }

  /**
   * RR cache constructor setting the maximum cache size to the given size and caching only data that is tested true 
   * for the given caching rule.
   * @param maxCacheSize Maximum cache size in bytes.
   * @param cachingRule Cache rule determining which data will be cached.
   */
  public RandomReplacement(long maxCacheSize, Predicate<T> cachingRule) {
    this(null, maxCacheSize, cachingRule);
  }

  /**
   * RR cache constructor setting the maximum cache size to the given size and caching only data that is tested true 
   * for the given caching rule. This constructor also lets you set the underlying storage, queried if an entry is not 
   * found in the cache.
   * @param cachedStorage Storage object to query if an entry is not found in the cache.
   * @param maxCacheSize Maximum cache size in bytes.
   * @param cachingRule Cache rule determining which data will be cached.
   */
  public RandomReplacement(IDataStorage<T> cachedStorage, long maxCacheSize,
      Predicate<T> cachingRule) {
    super(cachedStorage, maxCacheSize, cachingRule);
  }

  /**
   * RR cache constructor setting the maximum cache size to the given size and caching only data that is tested true 
   * for the given caching rule. This constructor also lets you set the underlying storage, queried if an entry is not 
   * found in the cache. The given seed will be used for the random number generator to behave deterministically.
   * @param cachedStorage Storage object to query if an entry is not found in the cache.
   * @param maxCacheSize Maximum cache size in bytes.
   * @param cachingRule Cache rule determining which data will be cached.
   * @param seed Specified seed for the random number generator.
   */
  public RandomReplacement(IDataStorage<T> cachedStorage, long maxCacheSize,
      Predicate<T> cachingRule, long seed) {
    super(cachedStorage, maxCacheSize, cachingRule);
    setSeed(seed);
  }

  /**
   * Sets the seed for the random number generator to the given value.
   * @param seed Specified seed for the random number generator.
   */
  public void setSeed(long seed) {
    rand.setSeed(seed);
  }

  @Override
  protected void removeEntryByCachingStrategy() {
    dataRemovedFromCache(getEntries().remove(rand.nextInt(getEntries().size())).getByteSize());
  }

}