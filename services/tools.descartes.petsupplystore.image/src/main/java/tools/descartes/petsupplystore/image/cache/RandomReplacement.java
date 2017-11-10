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
package tools.descartes.petsupplystore.image.cache;

import java.util.Random;
import java.util.function.Predicate;

import tools.descartes.petsupplystore.image.cache.entry.ICachable;
import tools.descartes.petsupplystore.image.cache.rules.CacheAll;
import tools.descartes.petsupplystore.image.storage.IDataStorage;

public class RandomReplacement<T extends ICachable<T>> extends AbstractQueueCache<T> {

	private Random rand = new Random();
	
	public RandomReplacement() {
		this(IDataCache.STD_MAX_CACHE_SIZE);
	}
	
	public RandomReplacement(long maxCacheSize) {
		this(maxCacheSize, new CacheAll<T>());
	}
	
	public RandomReplacement(long maxCacheSize, Predicate<T> cachingRule) {
		this(null, maxCacheSize, cachingRule);
	}
	
	public RandomReplacement(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(cachedStorage, maxCacheSize, cachingRule);
	}
	
	public RandomReplacement(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule, long seed) {
		super(cachedStorage, maxCacheSize, cachingRule);
		setSeed(seed);
	}
	
	public void setSeed(long seed) {
		rand.setSeed(seed);
	}

	@Override
	protected void removeEntryByCachingStrategy() {
		dataRemovedFromCache(entries.remove(rand.nextInt(entries.size())).getByteSize());
	}

}
