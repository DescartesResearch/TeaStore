package tools.descartes.petstore.image.cache;

import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.CountedEntry;
import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.cache.rules.CacheAll;
import tools.descartes.petstore.image.storage.IDataStorage;

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
				(a,b) -> a.getUseCount() - b.getUseCount() != 0 ? a.getUseCount() - b.getUseCount() 
						: (a.getId() < b.getId() ? -1 : (a.getId() == b.getId() ? 0 : 1)));
	}

	@Override
	protected CountedEntry<T> createEntry(T data) {
		return new CountedEntry<T>(data);
	}

}
