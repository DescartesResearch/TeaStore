package tools.descartes.petstore.image.cache;

import tools.descartes.petstore.image.cache.entry.ICachable;
import tools.descartes.petstore.image.storage.IDataStorage;

public interface IDataCache<T extends ICachable<T>> extends IDataStorage<T> {
	
	public static final long STD_MAX_CACHE_SIZE = 10 * 1024 * 1024;
	
	public long getMaxCacheSize();
	
	public long getCurrentCacheSize();
	
	public long getFreeSpace();
	
	public boolean hasStorageFor(long size);
	
	public void cacheData(T data);
	
	public void uncacheData(T data);
	
	public boolean dataIsCachable(T data);
	
	public boolean dataIsInCache(long id);
	
	public void clearCache();
	
}
