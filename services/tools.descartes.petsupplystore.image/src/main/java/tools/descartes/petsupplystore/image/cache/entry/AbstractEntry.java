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
package tools.descartes.petsupplystore.image.cache.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base wrapper class for cachable data types.
 * @author Norbert Schmitt
 *
 * @param <D> Cachable data that must implement {@link tools.descartes.petsupplystore.image.cache.entry.ICachable}
 */
public abstract class AbstractEntry<D extends ICachable<D>> {
	
	private D data;
	private Logger log = LoggerFactory.getLogger(AbstractEntry.class);
	
	/**
	 * Basic constructor storing the cachable data. If the cachable data supplied is null, a 
	 * {@link java.lang.NullPointerException} is thrown.
	 * @param data Cachable data
	 */
	public AbstractEntry(D data) {
		if (data == null) {
			log.error("The supplied data is null.");
			throw new NullPointerException("Supplied data is null.");
		}
		
		this.data = data;
	}

	/**
	 * Returns the cachable data stored in this wrapper class.
	 * @return The cachable data
	 */
	public D getData() {
		return data;
	}
	
	/**
	 * Method signaling to the wrapper that this entry was read from the cache.
	 */
	public abstract void wasUsed();
	
	/**
	 * Returns the unique ID that each cachable data has.
	 * @return The unique ID for the cachable data
	 */
	public long getId() {
		return data.getId();
	}
	
	/**
	 * Returns the byte size of the cachable data.
	 * @return The byte size of the cachable data
	 */
	public long getByteSize() {
		return data.getByteSize();
	}
	
}
