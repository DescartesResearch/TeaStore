package tools.descartes.petstore.image.cache;

import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.cache.entry.TimedEntry;
import tools.descartes.petstore.image.cache.rules.CacheAll;
import tools.descartes.petstore.image.storage.IDataStorage;

public class LeastRecentlyUsed<T extends ICachable<T>> extends AbstractTreeCache<T, TimedEntry<T>> {
	
	public LeastRecentlyUsed() {
		this(IDataCache.STD_MAX_CACHE_SIZE);
	}
	
	public LeastRecentlyUsed(long maxCacheSize) {
		this(maxCacheSize, new CacheAll<T>());
	}
	
	public LeastRecentlyUsed(long maxCacheSize, Predicate<T> cachingRule) {
		this(null, maxCacheSize, cachingRule);
	}
	
	public LeastRecentlyUsed(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(cachedStorage, maxCacheSize, cachingRule, (a,b) -> a.getTime() - b.getTime() < 0 
				? -1 : (a.getTime() - b.getTime() > 0 ? 1 : (a.getId() < b.getId() 
						? -1 : (a.getId() == b.getId() ? 0 : 1))));
	}

	@Override
	protected TimedEntry<T> createEntry(T data) {
		return new TimedEntry<>(data);
	}

}
