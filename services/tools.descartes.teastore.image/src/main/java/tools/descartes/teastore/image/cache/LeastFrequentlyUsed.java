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

import tools.descartes.teastore.image.cache.entry.CountedEntry;
import tools.descartes.teastore.image.cache.entry.ICachable;
import tools.descartes.teastore.image.cache.rules.CacheAll;
import tools.descartes.teastore.image.storage.IDataStorage;

public class LeastFrequentlyUsed<T extends ICachable<T>> extends AbstractTreeCache<T, CountedEntry<T>> {
	
	public LeastFrequentlyUsed() {
		this(IDataCache.STD_MAX_CACHE_SIZE);
	}
	
	public LeastFrequentlyUsed(long maxCacheSize) {
		this(maxCacheSize, new CacheAll<T>());
	}
	
	public LeastFrequentlyUsed(long maxCacheSize, Predicate<T> cachingRule) {
		this(null, maxCacheSize, cachingRule);
	}
	
	public LeastFrequentlyUsed(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(cachedStorage, maxCacheSize, cachingRule, 
				(a,b) -> a.getId() == b.getId() ? 0 
						: (a.getUseCount() - b.getUseCount() != 0 ? a.getUseCount() - b.getUseCount() 
								: (a.getId() < b.getId() ? -1 : 1)));
	}

	@Override
	protected CountedEntry<T> createEntry(T data) {
		return new CountedEntry<T>(data);
	}

}
