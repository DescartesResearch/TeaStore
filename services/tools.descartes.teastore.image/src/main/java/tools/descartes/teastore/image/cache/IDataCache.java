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

import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.storage.IDataStorage;

/**
 * Interface defining all functions available to a cache. This interface extends 
 * {@link tools.descartes.teastore.image.storage.IDataStorage} to allow multiple cache levels.
 * @author Norbert Schmitt
 *
 * @param <T> Entry Type implementing ICachable.
 */
public interface IDataCache<T extends ICachable<T>> extends IDataStorage<T> {

  /**
   * Standard cache size of 3MiB.
   */
  public static final long STD_MAX_CACHE_SIZE = 3 * 1024 * 1024;

  /**
   * Returns the maximum cache size in bytes.
   * @return Maximum cache size in bytes.
   */
  public long getMaxCacheSize();

  /**
   * Returns the current size of the data in the cache in bytes.
   * @return Current size of the data in the cache in bytes.
   */
  public long getCurrentCacheSize();

  /**
   * Returns the remaining space for data in bytes.
   * @return Remaining space for data in bytes.
   */
  public long getFreeSpace();

  /**
   * Checks if there is enough space left for the given size.
   * @param size Size to check for in bytes.
   * @return True if the cache has enough memory left or false otherwise.
   */
  public boolean hasStorageFor(long size);

  /**
   * Inserts data into the cache if allowed by the caching rule. Will evict one or more entries if not enough memory 
   * is left.
   * @param data Data object to cache.
   */
  public void cacheData(T data);

  /**
   * Remove data from cache.
   * @param data Data to be removed from cache.
   */
  public void uncacheData(T data);

  /**
   * Checks whether data is cachable according to the caching rule.
   * @param data Data to be checked if it is cachable.
   * @return True if the caching rule allows caching of the given object or false otherwise.
   */
  public boolean dataIsCachable(T data);

  /**
   * Checks whether a given ID resides in the cache.
   * @param id ID to check if it resides in the cache.
   * @return True if the data with the given ID resides in cache or false otherwise.
   */
  public boolean dataIsInCache(long id);

  /**
   * Resets the cache and deletes all elements from it.
   */
  public void clearCache();

  /**
   * Changes the maximum cache size. If the new cache size is smaller than before, entries could be evicted according 
   * to the eviction strategy.
   * @param cacheSize Positive maximum size of the cache in bytes.
   * @return True if the new cache size was successfully set or false otherwise.
   */
  public boolean setMaxCacheSize(long cacheSize);

}
