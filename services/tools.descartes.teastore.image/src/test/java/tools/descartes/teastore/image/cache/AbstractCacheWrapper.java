package tools.descartes.teastore.image.cache;

import java.util.ArrayList;
import java.util.function.Predicate;

import tools.descartes.teastore.image.StoreImage;
import tools.descartes.teastore.image.cache.entry.ICacheEntry;
import tools.descartes.teastore.image.storage.IDataStorage;

public final class AbstractCacheWrapper
    extends AbstractCache<ArrayList<ICacheEntry<StoreImage>>, StoreImage, ICacheEntry<StoreImage>> {

  public AbstractCacheWrapper(ArrayList<ICacheEntry<StoreImage>> entries,
      IDataStorage<StoreImage> cachedStorage, long maxCacheSize,
      Predicate<StoreImage> cachingRule) {
    super(entries, cachedStorage, maxCacheSize, cachingRule);
  }

  @Override
  protected void removeEntryByCachingStrategy() {
    dataRemovedFromCache(getEntries().remove(0).getByteSize());
  }

  @Override
  protected ICacheEntry<StoreImage> createEntry(StoreImage data) {
    return new DummyEntry(data);
  }

}
