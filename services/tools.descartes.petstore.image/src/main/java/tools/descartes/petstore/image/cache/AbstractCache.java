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
package tools.descartes.petstore.image.cache;

import java.util.Collection;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.petstore.image.cache.entry.AbstractEntry;
import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.cache.rules.CacheAll;
import tools.descartes.petstore.image.storage.IDataStorage;
import tools.descartes.petstore.image.storage.NoStorage;

public abstract class AbstractCache<S extends Collection<F>, T extends ICachable<T>, F extends AbstractEntry<T>> 
		implements IDataCache<T> {
	
	protected IDataStorage<T> cachedStorage;
	protected S entries;
	
	private long maxCacheSize;
	private long currentCacheSize;
	private Predicate<T> cachingRule;
	private Logger log = LoggerFactory.getLogger(AbstractCache.class);
	
	public AbstractCache(S entries) {
		this(entries, IDataCache.STD_MAX_CACHE_SIZE);
	}
	
	public AbstractCache(S entries, long maxCacheSize) {
		this(entries, maxCacheSize, new CacheAll<T>());
	}
	
	public AbstractCache(S entries, long maxCacheSize, Predicate<T> cachingRule) {
		this(entries, null, maxCacheSize, cachingRule);
	}
	
	public AbstractCache(S entries, IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		if (entries == null) {
			log.error("The provided internal storage object is null.");
			throw new NullPointerException("The provided internal storage object is null.");
		}
		if (maxCacheSize < 0) {
			log.error("The provided cache size is negative. Must be positive.");
			throw new IllegalArgumentException("The provided cache size is negative. Must be positive.");
		}
		if (cachingRule == null) {
			log.error("The provided caching rule is null.");
			throw new NullPointerException("The provided caching rule is null.");
		}
	
		if (cachedStorage == null) {
			log.info("No underlying disk storage supplied, assuming no data is stored on disk.");
			this.cachedStorage = new NoStorage<T>();
		} else {
			this.cachedStorage = cachedStorage;
		}
		this.entries = entries;
		this.maxCacheSize = maxCacheSize;
		this.currentCacheSize = 0;
		this.cachingRule = cachingRule;
	}
	
	private T getData(long id) {
		F data = entries.stream().filter(entry -> entry.getId() == id).findFirst().orElse(null);
		return data == null ? null : data.getData();
	}
	
	/*
	 * Implementations for interface IDataCache
	 */
	
	@Override
	public long getMaxCacheSize() {
		return maxCacheSize;
	}
	
	@Override
	public long getCurrentCacheSize() {
		return currentCacheSize;
	}
	
	@Override
	public long getFreeSpace() {
		return maxCacheSize - currentCacheSize;
	}
	
	@Override
	public boolean hasStorageFor(long size) {
		return size < getFreeSpace();
	}

	@Override
	public void cacheData(T data) {
		if (!dataIsCachable(data) || dataIsInCache(data.getId())) {
			return;
		}
		
		if (data.getByteSize() > maxCacheSize) {
			return;
		}
		
		while (!hasStorageFor(data.getByteSize())) {
			removeEntryByCachingStrategy();
		}
		addEntry(createEntry(data));
	}
	
	@Override
	public void uncacheData(T data) {
		entries.remove(createEntry(data));
		dataRemovedFromCache(data.getByteSize());
	}
	
	@Override
	public boolean dataIsCachable(T data) {
		return cachingRule.test(data);
	}

	@Override
	public boolean dataIsInCache(long id) {
		return getData(id) != null;
	}
	
	@Override
	public void clearCache() {
		entries.clear();
		currentCacheSize = 0;
	}
	
	/*
	 * Implementations for interface IDataStorage
	 */
	
	@Override
	public boolean dataExists(long id) {
		return dataIsInCache(id) ? cachedStorage.dataExists(id) : false;
	}
	
	@Override
	public T loadData(long id) {
		// Search entry in cache
		T entry = getData(id);
		if (entry == null) {
			// No entry in cache found, search in underlying storage
			entry = cachedStorage.loadData(id);
			if (entry == null) {
				return null;
			}
			// Image found, cache it and return
			cacheData(entry);
		}
		// Image found in cache, increment and return
		return entry;
	}
	
	@Override
	public boolean saveData(T data) {
		if (data == null) {
			return false;
		}
	
		cacheData(data);
		return cachedStorage.saveData(data);
	}

	@Override
	public boolean dataIsStorable(T data) {
		return cachedStorage.dataIsStorable(data);
	}
	
	@Override
	public boolean deleteData(T data) {
		uncacheData(data);
		return cachedStorage.deleteData(data);
	}
	
	/*
	 * Modifier for current cache size
	 */
	
	protected void dataRemovedFromCache(long size) {
		// This case should not happen
		if (size > currentCacheSize) {
			currentCacheSize = 0;
		}
		currentCacheSize -= size;
	}
	
	protected void dataAddedToCache(long size) {
		currentCacheSize += size;
	}
	
	/*
	 * Abstract methods to store data that is implementation specific
	 */
	
	protected abstract F createEntry(T data);
	
	protected void addEntry(F data) {
		entries.add(data);
		dataAddedToCache(data.getByteSize());
	}

	protected abstract void removeEntryByCachingStrategy();
	
}
