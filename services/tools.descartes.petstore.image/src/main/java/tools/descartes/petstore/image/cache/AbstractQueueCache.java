package tools.descartes.petstore.image.cache;

import java.util.LinkedList;
import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.cache.entry.SimpleEntry;
import tools.descartes.petstore.image.storage.IDataStorage;

public abstract class AbstractQueueCache<T extends ICachable<T>> 
		extends AbstractCache<LinkedList<SimpleEntry<T>>, T, SimpleEntry<T>> {
	
	public AbstractQueueCache() {
		super(new LinkedList<>());
	}
	
	public AbstractQueueCache(long maxCacheSize) {
		super(new LinkedList<>(), maxCacheSize);
	}
	
	public AbstractQueueCache(long maxCacheSize, Predicate<T> cachingRule) {
		super(new LinkedList<>(), maxCacheSize, cachingRule);
	}
	
	public AbstractQueueCache(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule) {
		super(new LinkedList<>(), cachedStorage, maxCacheSize, cachingRule);
	}
	
	/*
	 * Implementations of abstract superclass
	 */
	
	@Override
	public SimpleEntry<T> createEntry(T data) {
		return new SimpleEntry<T>(data);
	}
	
	@Override
	protected abstract void removeEntryByCachingStrategy();
	
}
