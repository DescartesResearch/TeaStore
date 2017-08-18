package tools.descartes.petstore.image.cache;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Predicate;

import tools.descartes.petstore.image.cache.entry.AbstractEntry;
import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.storage.IDataStorage;

public abstract class AbstractTreeCache<T extends ICachable<T>, F extends AbstractEntry<T>> extends AbstractCache<TreeSet<F>, T, F> {

	public AbstractTreeCache(Comparator<F> ordering) {
		super(new TreeSet<>(ordering));
	}
	
	public AbstractTreeCache(long maxCacheSize, Comparator<F> ordering) {
		super(new TreeSet<>(ordering), maxCacheSize);
	}
	
	public AbstractTreeCache(long maxCacheSize, Predicate<T> cachingRule, Comparator<F> ordering) {
		super(new TreeSet<>(ordering), maxCacheSize, cachingRule);
	}
	
	public AbstractTreeCache(IDataStorage<T> cachedStorage, long maxCacheSize, Predicate<T> cachingRule, 
			Comparator<F> ordering) {
		super(new TreeSet<>(ordering), cachedStorage, maxCacheSize, cachingRule);
	}
	
	protected abstract F createEntry(T data);
	
	@Override
	protected void removeEntryByCachingStrategy() {
		dataRemovedFromCache(entries.pollFirst().getByteSize());
	}
	
}
