package tools.descartes.petstore.image.cache;

import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.storage.IDataStorage;

public class FirstInFirstOut<T extends ICachable<T>> extends AbstractQueueCache<T> {
	
	public FirstInFirstOut() {
		super();
	}
	
	public FirstInFirstOut(long maxCacheSize) {
		super(maxCacheSize);
	}
	
	public FirstInFirstOut(long maxCacheSize, Predicate<T> cachingRule) {
		super(maxCacheSize, cachingRule);
	}
	
	public FirstInFirstOut(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(cachedStorage, maxCacheSize, cachingRule);
	}
	
	@Override
	protected void removeEntryByCachingStrategy() {
		T data = entries.pollFirst().getData();
		dataRemovedFromCache(data.getByteSize());
	}
	
}
