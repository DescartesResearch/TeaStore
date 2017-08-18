package tools.descartes.petstore.image.cache;

import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.storage.IDataStorage;

public class LastInFirstOut<T extends ICachable<T>> extends AbstractQueueCache<T> {

	public LastInFirstOut() {
		super();
	}
	
	public LastInFirstOut(long maxCacheSize) {
		super(maxCacheSize);
	}
	
	public LastInFirstOut(long maxCacheSize, Predicate<T> cachingRule) {
		super(maxCacheSize, cachingRule);
	}
	
	public LastInFirstOut(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(cachedStorage, maxCacheSize, cachingRule);
	}

	@Override
	protected void removeEntryByCachingStrategy() {
		T data = entries.pollLast().getData();
		dataRemovedFromCache(data.getByteSize());
	}

}
