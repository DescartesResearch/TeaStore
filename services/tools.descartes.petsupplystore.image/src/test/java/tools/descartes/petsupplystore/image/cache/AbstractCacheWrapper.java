package tools.descartes.petsupplystore.image.cache;

import java.util.ArrayList;
import java.util.function.Predicate;

import tools.descartes.petsupplystore.image.StoreImage;
import tools.descartes.petsupplystore.image.cache.entry.ICacheEntry;
import tools.descartes.petsupplystore.image.storage.IDataStorage;

public final class AbstractCacheWrapper extends AbstractCache<ArrayList<ICacheEntry<StoreImage>>, StoreImage, ICacheEntry<StoreImage>> {

	public AbstractCacheWrapper(ArrayList<ICacheEntry<StoreImage>> entries, IDataStorage<StoreImage> cachedStorage,
			long maxCacheSize, Predicate<StoreImage> cachingRule) {
		super(entries, cachedStorage, maxCacheSize, cachingRule);
	}

	@Override
	protected void removeEntryByCachingStrategy() {
		dataRemovedFromCache(entries.remove(0).getByteSize());
	}

	@Override
	protected ICacheEntry<StoreImage> createEntry(StoreImage data) {
		return new DummyEntry(data);
	}

}
