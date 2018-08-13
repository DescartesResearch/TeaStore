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
package tools.descartes.teastore.image.cache.entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base wrapper class for cachable data types.
 * 
 * @author Norbert Schmitt
 *
 * @param <D>
 *          Cachable data that must implement
 *          {@link tools.descartes.teastore.image.cache.entry.ICachable}
 */
public abstract class AbstractEntry<D extends ICachable<D>> implements ICacheEntry<D> {

  private D data;
  private Logger log = LoggerFactory.getLogger(AbstractEntry.class);

  /**
   * Basic constructor storing the cachable data. If the cachable data supplied is
   * null, a {@link java.lang.NullPointerException} is thrown.
   * 
   * @param data
   *          Cachable data
   */
  public AbstractEntry(D data) {
    if (data == null) {
      log.error("The supplied data is null.");
      throw new NullPointerException("Supplied data is null.");
    }

    this.data = data;
  }

  @Override
  public D getData() {
    return data;
  }

  @Override
  public abstract void wasUsed();

  @Override
  public long getId() {
    return data.getId();
  }

  @Override
  public long getByteSize() {
    return data.getByteSize();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (data == null) {
      result = prime * result + 0;
    } else {
      result = prime * result + data.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AbstractEntry<?> other = (AbstractEntry<?>) obj;
    if (data == null) {
      if (other.data != null) {
        return false;
      }
    } else if (!data.equals(other.data)) {
      return false;
    }
    return true;
  }

}
