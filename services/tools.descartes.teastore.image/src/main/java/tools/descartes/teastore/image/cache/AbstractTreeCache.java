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

public abstract class AbstractTreeCache<T extends ICachable<T>, F extends ICacheEntry<T>> extends AbstractCache<TreeSet<F>, T, F> {
	
	public AbstractTreeCache(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule, 
			Comparator<F> ordering) {
		super(new TreeSet<>(ordering), cachedStorage, maxCacheSize, cachingRule);
	}
	
	protected abstract F createEntry(T data);
	
	@Override
	protected void removeEntryByCachingStrategy() {
		dataRemovedFromCache(entries.pollFirst().getByteSize());
	}
	
	@Override
	protected void reorderAndTag(F data) {
		entries.remove(data);
		data.wasUsed();
		entries.add(data);
	}
	
}
