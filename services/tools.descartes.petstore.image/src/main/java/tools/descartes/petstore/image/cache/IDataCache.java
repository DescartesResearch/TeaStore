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
